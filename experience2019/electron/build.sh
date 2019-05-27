#! /usr/bin/env sh

# This is a dump script that compiles and flashes the electron.ino file to the
# attiny85. It really is a pain for me to connect to the programmer with the
# IDE, not sure why... This is damn ugly, but allows me to work...

set -e
set -u
set -x

mkdir -p electron_build_cache
mkdir -p electron_build_path

$HOME/Downloads/arduino-1.8.9/arduino-builder \
  -compile \
  -logger=machine \
  -hardware $HOME/Downloads/arduino-1.8.9/hardware \
  -hardware $HOME/.arduino15/packages \
  -tools $HOME/Downloads/arduino-1.8.9/tools-builder \
  -tools $HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -tools $HOME/.arduino15/packages \
  -built-in-libraries $HOME/Downloads/arduino-1.8.9/libraries \
  -libraries $HOME/.arduino/libraries \
  -fqbn=attiny:avr:ATtinyX5:cpu=attiny85,clock=internal1 \
  -ide-version=10809 \
  -build-path electron_build_path \
  -warnings=none \
  -build-cache electron_build_cache \
  -prefs=build.warn_data_percentage=75 \
  -prefs=runtime.tools.arduinoOTA.path=$HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -prefs=runtime.tools.arduinoOTA-1.2.1.path=$HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -prefs=runtime.tools.avr-gcc.path=$HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -prefs=runtime.tools.avr-gcc-5.4.0-atmel3.6.1-arduino2.path=$HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -prefs=runtime.tools.avrdude.path=$HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -prefs=runtime.tools.avrdude-6.3.0-arduino14.path=$HOME/Downloads/arduino-1.8.9/hardware/tools/avr \
  -verbose \
  electron.ino


avrdude -c stk500v1 -p attiny85 -P /dev/ttyUSB0  -b 19200 -U flash:w:electron_build_path/electron.ino.hex
