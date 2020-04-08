#!/bin/bash

# 删除git里所有的*.iml文件


for d in `ls` ; do
  if [ -d $d ] ; then
    imlFile=${d}/*.iml
    echo "rm:$imlFile"
    git rm --cached ${imlFile}
  fi
done