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
  - [Find the target (5 points)](#find-the-target-5-points)
    - [Challenge One (2 of 5 points)](#challenge-one-2-of-5-points)
    - [Challenge Two (2 of 5 points)](#challenge-two-2-of-5-points)
    - [Challenge Three (1 of 5 points)](#challenge-three-1-of-5-points)
  - [Determine angle and range to target (10 points)](#determine-angle-and-range-to-target-10-points)
  - [Calculate robot's field position (15 points)](#calculate-robots-field-position-15-points)
- [Scoring](#scoring)
- [Team Information](#team-information)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Objectives

There are three objectives in this event, each incrementally more challenging
then the previous. Each objective gives points towards winning the computer
vision event.

This repository contains a template robot project to get you started as well as
sample target images you will use while developing and testing your solutions.

The sample images were taken of a 2020-21 Infinite Recharge target with the
Deadeye vision system and also come with data regarding the field position they
were taken from. You can use these sample images to check the accuracy of your
programs; just be aware—new photos will be provided for the actual event!

### Find the target (5 points)

Given an image, how well can you screen out false targets and isolate the real
target? This filtering should work from multiple angles and distances.

Points are awarded for obtaining accurate data returned by the Deadeye system.

#### Challenge One (2 of 5 points)

This first challenge will get you familiar with using the Deadeye dashboard and
getting target data to your robot program. You will simply need to complete
Deadeye's [Quickstart](https://deadeye.readthedocs.io/en/latest/) walkthrough
to get two points!

#### Challenge Two (2 of 5 points)

This challenge will use the target data returned from Deadeye in a simulated
robot shooting command.

1. For this challenge, configure the Deadeye dashboard to return target data
   from the large **U-shaped** target in the provided test image.

2. Create or reuse a robot program with a configured ``DeadeyeX0`` class (where
   ``X`` is the Deadeye unit you are using).

3. Create a ``ShooterSubsystem`` and a ``ShootCommand`` in your robot program.

4. The ``ShootCommand`` calls ``ShooterSubsystem.shoot()`` when a controller
   button is pressed.

5. When the ``ShooterSubsystem.shoot()`` method is called, it will use the
   ``DeadeyeX0`` object to retrieve the U-shaped target coordinates and print
   them in the log.

#### Challenge Three (1 of 5 points)

Repeat challenge two, this time returning target data from the large **vertical
line** target in the provided test image.

### Determine angle and range to target (10 points)

Use the data returned by the Deadeye system to calculate the angle and range to
the target with respect to the robot.

Points are awarded based on the least angle and range error.

### Calculate robot's field position (15 points)

Having calculated angle and range to the target, can you calculate the position
of the robot on the field?

Points are awarded based on the least position error.

## Scoring

The following points are awarded to all participating teams in order of finish
and applied towards the overall Olympics score.

1. first place - 20 points
2. second place - 15 points
3. third place - 10 points
4. fourth place - 5 points

In the case of a tie, the average of the available points will be given to each
team. For example, a tie for first place will award the average of the first
and second place points to each team (N points).

## Team Information

You will use the Deadeye vision system's web dashboard to configure the camera
to detect the target and filter out false targets.

Each team will have access to their own Deadeye unit:

- Team A: [deadeye-h](http://192.168.3.10)
- Team B: [deadeye-i](http://192.168.3.11)
- Team C: [deadeye-j](http://192.168.3.12)
- Team D: [deadeye-k](http://192.168.3.13)

There are three cameras available in each Deadeye unit, each configured with
one of the standard vision processing pipelines. For example, Deadeye unit H
has:

- `H0` - UprightRectPipeline
- `H1` - MinAreaRectPipeline
- `H2` - TargetListPipeline

