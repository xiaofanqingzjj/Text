#!/bin/bash

# 删除git里所有的*.iml文件


function  delImlFile() {
  local currentPath=$1

  # 指定目录下的所有的文件
  for f in `ls $currentPath`; do

    local f2="$currentPath/$f"
    if [ -d $f2 ]; then
      # 如果是目录，递归调用当前函数
      delImlFile $f2
    elif [[ $f = *.iml ]]; then
      # 如果文件名是已iml结尾。注意这个比较，是在双方括号内部，匹配得字符串没有用引号扩起来
      echo "rm:$f2"
      git rm --cached $f2
    fi
  done
}

# 从当前目录开始搜索
delImlFile .
