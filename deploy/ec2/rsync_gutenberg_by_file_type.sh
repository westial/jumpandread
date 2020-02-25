#!/bin/bash

rsync -av --include="*/" --include="*.html" --include="*.htm" --include="*.txt" --include="*.jpeg" --include="*.jpg" --include="*.png" --include="*.gif" --exclude="*" --exclude="old" --del aleph.gutenberg.org::gutenberg /home/ec2-user/gutenberg
