/*
 * atTiny85
 *
 *      .-u-.
 *      |1|8| Vcc
 * PIN3 |2|7|
 *      |3|6| PIN1
 *  GND |4|5| PIN0
 *      '---'
 */

#define PIN_SENSOR 3    /* PIN3 */
#define PIN_MOTOR 1     /* PIN1 */
#define PIN_LED 0       /* PIN0 */

/* Duration (in milliseconds) during which the motor must be turned on */
#define MOTOR_ACTIVATION_TIME_MS 10000 /* 10 s */


static inline boolean
detects_magnetic_field(void)
{
  return digitalRead(PIN_SENSOR) == 0;
}

static inline void
led_toggle(void)
{
  static boolean is_on = false;
  digitalWrite(PIN_LED, (is_on) ? HIGH : LOW);
  is_on = ! is_on;
}

void setup(void)
{
  /* We read the input data from the sensor on PIN_SENSOR */
  pinMode(PIN_SENSOR, INPUT_PULLUP);

  /* We will turn on the motor by setting PIN_MOTOR to high. So at setup, we
   * must ensure a LOW signal */
  pinMode(PIN_MOTOR, OUTPUT);
  digitalWrite(PIN_MOTOR, LOW);

  /* Configure the debug LED as being OFF at startup */
  pinMode(PIN_LED, OUTPUT);
  digitalWrite(PIN_LED, LOW);
}

void loop(void)
{
  /* Before doing our job, we will blink slowly (change LED stage each second) */
  static unsigned int led_delay_count = 0u;
  static unsigned int led_trigger_count = 1000u;

  static boolean electron_is_triggered = false;
  if (detects_magnetic_field() && (! electron_is_triggered))
  {
    electron_is_triggered = true;

    /* Stay HIGH until we are done */
    digitalWrite(PIN_LED, HIGH);

    /* Turn on the motor for a given amount of time, then turn it off */
    digitalWrite(PIN_MOTOR, HIGH);
    delay(MOTOR_ACTIVATION_TIME_MS);
    digitalWrite(PIN_MOTOR, LOW);

    /* We have done our job: blink much faster */
    led_trigger_count = 250u;
  }

  /* Control how the LED is going to be toggled */
  if (led_delay_count >= led_trigger_count)
  {
    led_toggle();
    led_delay_count = 0u;
  }

  /* IDLE - sleep for 10ms */
  led_delay_count += 10u;
  delay(10);
}
