#include <Wire.h>
#include <FastLED.h>

/* Address of the SRF08 on the I2C bus
 *
 * WARNING: the doc says 0xE0, but we address is supposed to be on 7 bits.
 * For some reason, masking the 7th bit makes it work. So when we write 0x70,
 * we actually refer to the address 0xE0.
 */
#define SRF08_ADDR 0x70 /* actually 0xE0 */

/* If the moving mean of the measured distance is LESS than
 * MM_DISTANCE_THRESHOLD (strictly), the software will trigger the release of the
 * electron
 */
#define MM_DISTANCE_THRESHOLD 10u

/* Size of the buffer used to store measures involved in the moving mean of the
 * distance of the robot, as measured by the SRF08.
 * Make sure it is a power of 2, as we perform arithmetic on this value.
 */
#define MM_DISTANCE_BUFSIZE 16u

/* The SRF08 can have its maximum range restricted by writing the register #2
 * with a magic value that is calculated with the following formula (units are
 * in millimeters) :
 *
 *   SRF_MAX_RANGE_MM = (SRF_RANGE_REG * 43) + 43
 */
#define SRF_MAX_RANGE_MM 500u /* 50 cm */
#define SRF_RANGE_REG ((SRF_MAX_RANGE_MM - 43u) / 43u)

/* Output digital pin that triggers the electromagnet */
#define PIN_ELECTROMAGNET 2

/* LEDs wall configuration */
#define PIN_LED_WALL 5
#define NUM_LEDS     60
#define LED_TYPE     WS2812B
#define COLOR_ORDER  GRB
#define COLOR_PINK CRGB(252, 90, 220)
#define COLOR_BLACK CRGB(0, 0, 0)

/* Wall of LEDs */
static CRGB leds[NUM_LEDS];

/* A circular buffer used to perform moving means. */
static uint16_t mm_buffer[MM_DISTANCE_BUFSIZE];
static unsigned char mm_buffer_idx = 0u;

static inline void
leds_color_set(CRGB crgb)
{
  for (unsigned char i = 0u; i < NUM_LEDS; i++)
  { leds[i] = crgb; }
  FastLED.show();
}

static void
leds_init(void)
{
  FastLED.addLeds<
    LED_TYPE,
    PIN_LED_WALL,
    COLOR_ORDER
  >(leds, NUM_LEDS).setCorrection(TypicalLEDStrip);
  FastLED.setBrightness(255);
  leds_color_set(COLOR_BLACK);
}

/* Read the first echo received by the SRF08. */
static uint16_t
srf08_echo_read(void)
{
  uint16_t value = 0u;
  Wire.beginTransmission(SRF08_ADDR);
  Wire.write(byte(2)); /* Read Echo 1 */
  Wire.endTransmission();

  Wire.requestFrom(SRF08_ADDR, 2);
  if (Wire.available() >= 2) /* XXX Not sure why this is needed */
  {
    value = Wire.read();
    value <<= 8;
    value |= Wire.read();
  }
  return value;
}

static void
srf08_ranging_init(void)
{
  /* Initiate a ranging request */
  Wire.beginTransmission(SRF08_ADDR);
  Wire.write(byte(0)); /* Will write to register 0 */
  Wire.write(byte(81)); /* Receive results in cm */
  Wire.endTransmission(SRF08_ADDR);
}

static uint16_t
srf08_acquire(void)
{
  srf08_ranging_init();
  /* A ranging requires 65 ms to complete. At least that's the default
   * recommended value. However, the hardware is re-configured to perform
   * acquisitions in a restricted range. So in practise, the wait time will
   * be lower than 65 ms. We select 70 ms because we don't need frequent
   * acquisitions */
  delay(70);
  return srf08_echo_read();
}

static void
mm_distance_init(void)
{
  for (unsigned char i = 0u; i < MM_DISTANCE_BUFSIZE; i++)
  { mm_buffer[i] = srf08_acquire(); }
}

static uint16_t
mm_distance_calculate(void)
{
  /* Since we have restricted all the measures to AT the very most 800mm, we
   * will not receive values greater than 80 (cm). So we this measure, we can
   * sum at least 819 measures and still be sure we will not have an overflow.
   * So the sum we perform here is very safe! */
  uint16_t mm_distance = 0u;
  for (unsigned char i = 0u; i < MM_DISTANCE_BUFSIZE; i++)
  { mm_distance += mm_buffer[i]; }

  return mm_distance / MM_DISTANCE_BUFSIZE;
}

static void
mm_distance_add(const uint16_t measure)
{
  /* Add the measure to the circular buffer mm_buffer_idx */
  mm_buffer[mm_buffer_idx] = measure;
  mm_buffer_idx = (mm_buffer_idx + 1u) % MM_DISTANCE_BUFSIZE;
}

void setup(void)
{
  /* Disable the electromagnet. Always. */
  pinMode(PIN_ELECTROMAGNET, OUTPUT);
  digitalWrite(PIN_ELECTROMAGNET, LOW);

  /* Prepare LED lights */
  leds_init();
  Serial.begin(9600);

  /* Connect to the I2C bus as a master. We will initiate rangings and read
   * from the SRF08 Ultra sonic range finder */
  Wire.begin();

  /* Setup the maximum range of the SRF08 */
  Wire.beginTransmission(SRF08_ADDR);
  Wire.write(byte(2)); /* Will write to register 2 */
  Wire.write(byte(SRF_RANGE_REG));
  Wire.endTransmission(SRF08_ADDR);

  /* Now that the SRF08 is fully initialized, collect some measures to
   * populate the buffer that will be used to calculate the moving mean of the
   * distance where the robot is supposed to be. */
  mm_distance_init();
}

void loop(void)
{
  static boolean robot_is_gone = false;
  const uint16_t measure = srf08_acquire();
  mm_distance_add(measure);

  const uint16_t mm_distance = mm_distance_calculate();

  Serial.println(mm_distance); /* Debug */
  if ((mm_distance < MM_DISTANCE_THRESHOLD) && (! robot_is_gone))
  {
    robot_is_gone = true;
    /* The robot is gone: pink power */
    leds_color_set(COLOR_PINK);

    /* Start the electromagnet... full power. After 1 sec, disable the
     * electromagnet. */
    digitalWrite(PIN_ELECTROMAGNET, HIGH);
    delay(1000);
    digitalWrite(PIN_ELECTROMAGNET, LOW);
  }

  delay(10); /* IDLE for 10 ms */
}
