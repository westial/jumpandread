#!/bin/bash

aws s3 cp /home/ec2-user/gutenberg-index/sitemap.xml s3://com.westial.gutenberg.virginia
aws s3 sync /home/ec2-user/gutenberg-index/sitemaps s3://com.westial.gutenberg.virginia/sitemaps

