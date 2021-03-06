# Box

A [SRF08][1] sensor is in charge of detecting that the robot is not in its
starting area anymore. This ultra sonic range finder is configured to perform
acquisitions within a range of `SRF_MAX_RANGE_MM` (around 70 cm). To avoid
false detections due to the noise generated by the environment, we collect
`MM_DISTANCE_BUFSIZE` measures in a circular buffer. If the moving mean of the
measures of distance in this buffer is strictly less than
`MM_DISTANCE_THRESHOLD`, then an electromagnet will be turned on for one
second. The electromagnetic field generated by the electromagnet should be
detected by the electron so the experiment can be performed


## Dependencies

- FastLED by Daniel Garcia v3.2.1

[1]: https://www.robot-electronics.co.uk/htm/srf08tech.html
