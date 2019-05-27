/*
 * atTiny85
 *
 *      .-u-.
 *      |1|8| Vcc
 * PIN3 |2|7|
 *      |3|6| PIN1
 *  GND |4|5|
 *      '---'
 */

#define PIN_SENSOR 3    /* PIN3 */
#define PIN_MOTOR 1     /* PIN1 */

/* Duration (in milliseconds) during which the motor must be turned on */
#define MOTOR_ACTIVATION_TIME_MS 1000

static inline boolean
detects_magnetic_field(void)
{
  return digitalRead(PIN_SENSOR) == 0;
}

void setup(void)
{
  /* We read the input data from the sensor on PIN_SENSOR */
  pinMode(PIN_SENSOR, INPUT_PULLUP);

  /* We will turn on the motor by setting PIN_MOTOR to high. So at setup, we
   * must ensure a LOW signal */
  pinMode(PIN_MOTOR, OUTPUT);
  digitalWrite(PIN_MOTOR, LOW);
}

void loop(void)
{
  static boolean electron_is_triggered = false;
  if (detects_magnetic_field() && (! electron_is_triggered))
  {
    electron_is_triggered = true;

    /* Turn on the motor for a given amount of time, then turn it off */
    digitalWrite(PIN_MOTOR, HIGH);
    delay(MOTOR_ACTIVATION_TIME_MS);
    digitalWrite(PIN_MOTOR, LOW);
  }

  delay(10); /* IDLE a bit */
}
