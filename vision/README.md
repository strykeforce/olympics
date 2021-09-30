# Computer Vision Event
<!--
9/23 kick-off
11/11 end of competition
-->

Most seasons we use a camera on our robot to help us do things like
shoot at a goal or locate objectives on the field. Doing this quickly and
accurately without human involvement (or *autonomously*) is a key part of our
game plan.

The objective of this Stryke Force Olympics event is to learn how to use
computer vision to determine the position of simulated robot on the field.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Objectives](#objectives)
  - [Find the target](#find-the-target)
  - [Determine angle and range to target](#determine-angle-and-range-to-target)
  - [Calculate robot's field position](#calculate-robots-field-position)
- [Scoring](#scoring)
- [Getting Started](#getting-started)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Objectives

There are three objectives in this event, each a bit more challenging then the
previous. Each objective gives points towards winning the computer vision
event.

This repository contains a template robot project to get you started as well as
sample target images you will use while developing and testing your solutions.

The sample images were taken of a 2020-21 Infinite Recharge target with the
Deadeye vision system and also come with data regarding the field position they
were taken from. You can use these sample images to check the accuracy of your
programs; just be aware—new photos will be provided for the actual event!

### Find the target

Given an image, how well can you screen out false targets and isolate the real
target? This filtering should work from multiple angles and distances.

Points are awarded for obtaining accurate data returned by the Deadeye system.

See the [Getting Started](#getting-started) section for tips on sending data
from the Deadeye system to your robot code.

### Determine angle and range to target

Use the data returned by the Deadeye system to calculate the angle and range to
the target with respect to the robot.

Points are awarded based on the least angle and range error.

### Calculate robot's field position

Having calculated angle and range to the target, can you calculate the position
of the robot on the field?

Points are awarded based on the least position error.

## Scoring

The following points are awarded to all participating teams in order of finish
and applied towards the overall Olympics score.

1. W points
2. X points
3. Y points
4. Z points

In the case of a tie, the average of the available points will be given to each
team. For example, a tie for first place will award the average of the first
and second place points to each team (N points).

## Getting Started

You will use the Deadeye vision system's web dashboard to configure the camera
to detect the target and filter out false targets.

Access the Deadeye dashboard [here](http://192.168.3.10/).

There are several cameras available (`H0`, `H1`, ...) that you can configure to
detect the target and filter out false images.
