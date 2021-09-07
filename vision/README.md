# Computer Vision Event

The objective of this Stryke Force Olympics event is to learn how to use
computer vision to determine the position of robot on the field.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Objectives](#objectives)
  - [Find the target](#find-the-target)
  - [Determine angle and range to target](#determine-angle-and-range-to-target)
  - [Calculate robot's field position](#calculate-robots-field-position)
- [Scoring](#scoring)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Objectives

There are three objectives in this event, each more challenging then the
previous. Each objective gives points towards winning the computer vision
event.

This repository contains a template robot project to get you started as well as
sample target images. The sample images were taken of a 2020/21 Infinite
Recharge target with the Deadeye vision system and also come with data
regarding the field position they were taken from. You can use these sample
images to check the accuracy of your programs; new photos will be provided for
the actual event.

### Find the target

Given an image, how well can you screen out false reflections and isolate the
target of interest? This filtering should work from multiple angles and
distances.

Points are awarded for obtaining accurate data returned by the Deadeye system.

### Determine angle and range to target

Use the data returned by the Deadeye system to calculate the angle and range to
the target with respect to the robot.

Points are awarded for based on the angle and range error.

### Calculate robot's field position

Having calculated angle and range to the target, can you calculate the position
of the robot on the field?

Points are awarded for based on the position error.

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
