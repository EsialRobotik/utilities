#include <FastLED.h>

#define PIN_LED      5
#define PIN_SWITCH   3
#define PIN_ELECTRON 2
#define NUM_LEDS     60
#define LED_TYPE     WS2812B
#define COLOR_ORDER  GRB

#define COLOR_PINK CRGB(252, 90, 220)
#define COLOR_BLACK CRGB(0, 0, 0)

CRGB leds[NUM_LEDS];

/**
 * Set the color of the LEDs
 */
void setColor(CRGB crgb)
{
    for (int i=0; i<NUM_LEDS; i++) {
      leds[i] = crgb;
    }
    FastLED.show();
}

/**
 * Check that the switch is on the OFF position at boot
 * If not, it blocks and make the LEDs to blink until the switch is set to OFF
 */
void checkSwitchAtBoot() {
  while (digitalRead(PIN_SWITCH)) {
    setColor(COLOR_PINK);
    delay(250);
    setColor(COLOR_BLACK);
    delay(250);
  }

  delay(10);
}

/*
 * Block until the switch is switched ON
 */
void watchSwitch() {
  static boolean switchFired = false;

  if (switchFired) {
    return;
  }

  if (!digitalRead(PIN_SWITCH)) {
    return;
  }

  switchFired= true;
  digitalWrite(PIN_ELECTRON, HIGH);
  setColor(COLOR_PINK);
}

void setup() {
    delay( 1000 ); // power-up safety delay
    pinMode(PIN_SWITCH, INPUT);
    pinMode(PIN_ELECTRON, OUTPUT);
    pinMode(PIN_ELECTRON, LOW);
    FastLED.addLeds<LED_TYPE, PIN_LED, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
    FastLED.setBrightness(255);

    setColor(COLOR_BLACK);
    checkSwitchAtBoot();
}


void loop()
{
  watchSwitch();
  delay(10);
}

