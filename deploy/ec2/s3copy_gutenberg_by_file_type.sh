#!/bin/bash

aws s3 cp /home/ec2-user/gutenberg s3://com.westial.gutenberg.virginia --include="*/" --include="*.html" --include="*.htm" --include="*.txt" --include="*.jpeg" --include="*.jpg" --include="*.png" --include="*.gif" --exclude="*"
