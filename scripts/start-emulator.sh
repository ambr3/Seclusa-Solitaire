#!/usr/bin/env bash
# Start Pixel_9a (or Solitaire_Test fallback), wait for boot, install debug APK.
set -euo pipefail
source "$HOME/dev/config/env.sh"
export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$PATH"

AVD="${1:-Pixel_9a}"
if ! emulator -list-avds | grep -qx "$AVD"; then
  AVD=Solitaire_Test
  export ANDROID_AVD_HOME="${ANDROID_AVD_HOME:-$HOME/dev/projects/Solitaire/.avd}"
fi

# Clear stale snapshot locks that block boot
AVD_DIR="${ANDROID_AVD_HOME:-$HOME/dev/config/android/avd}/${AVD}.avd"
rm -f "$AVD_DIR"/*.lock "$AVD_DIR"/snapshot.trace "$AVD_DIR"/multiinstance.lock 2>/dev/null || true

echo "Starting AVD: $AVD"
emulator -avd "$AVD" -no-snapshot -netdelay none -netspeed full >/tmp/emulator-solitaire.log 2>&1 &
EMU_PID=$!
echo "emulator pid=$EMU_PID (log: /tmp/emulator-solitaire.log)"

echo -n "Waiting for device"
adb wait-for-device
until [[ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
  echo -n .
  sleep 2
done
echo " boot complete"

cd "$HOME/dev/projects/Solitaire"
./gradlew installDebug
adb shell am start -n com.ambr3.seclusasolitaire/de.tobiasbielefeld.solitaire.ui.GameSelector
echo "Installed and launched Seclusa Solitaire."
