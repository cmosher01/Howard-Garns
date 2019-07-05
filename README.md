# Howard-Garns

Copyright © 2005, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![License](https://img.shields.io/github/license/cmosher01/Howard-Garns.svg)](https://www.gnu.org/licenses/gpl.html)
[![Latest Release](https://img.shields.io/github/release-pre/cmosher01/Howard-Garns.svg)](https://github.com/cmosher01/Howard-Garns/releases/latest)
[![Build Status](https://travis-ci.com/cmosher01/Howard-Garns.svg?branch=master)](https://travis-ci.com/cmosher01/Howard-Garns)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)

Howard Garns invented “Number Place”, which is now known as “Sudoku” or “数独”.

I wrote the **Howard-Garns** application to replace pencil and paper (and eraser)
while you solve Sudoku puzzles.

# Simple Instructions

## New Puzzle

Use **File&nbsp;>&nbsp;New** to enter the numbers for the puzzle you want to solve.

Here is a simple puzzle to get you started. Just select the numbers below, **Ctrl-C** to copy, then
run the program, choose **File&nbsp;>&nbsp;Paste&nbsp;as&nbsp;New**

    100000239
    204000000
    003506400
    040002160
    680300002
    009800007
    000209600
    060000005
    408000900

You should look at other web sites for games in this easy text format, so you can just
copy and paste. Otherwise, you can manually enter the numbers for each square, using
zero for the empty squares.

## Possibilities

Each square on the board shows the remaining possibilities for that square. Left-click to
eliminate a possiblity after you have logically determined that that number cannot appear
in that square. Alternatively, right-click on a possibility to affirm that that square
does indeed contain that number.

## Rote Solving Assistants

The **Solve** menu lets you have the program do some of the rote logical patterns, such as
eliminating possibilities from the same row, column, or box as an already identified answer.
The different solvers are listed under the **Solve** menu; you can check the ones you want to
have enabled, and uncheck the ones you want turned off.

There are two ways to have the program solve for you: manually or automatically, which
you can choose at the top of the **Solve** menu.
*Manually* means that you choose **Solve** from the **Solve** menu when you want the program to
apply the (check-marked) solution patterns.
*Automatically* means that the program will apply them after every move you make. Note that
this may lead to some confusing results, in which case you may want to try turning off the
automatic solving.

## Undo and Redo

The program provides undo and redo support, under the **Edit** menu. You can undo all your
previous moves, or redo them again up to any point.

## Saving and Resuming

In the **File** menu, you can use **Save**, **Save&nbsp;As…**, and **Open…**, to save and resume the puzzle. All your moves
are saved, from the initial puzzle state up to the current state of the puzzle. The file
is a text file. Each move also has the time the move was made saved with it.
