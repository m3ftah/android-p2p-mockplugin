#!/usr/bin/python3

import os
import sys
import subprocess
import argparse
import json
import shutil

node = sys.argv[1]
ANDROFLEET_PATH = sys.argv[2]


RESULTS = ANDROFLEET_PATH + "/results"

class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

# Android

if not os.path.exists(RESULTS):
    os.makedirs(RESULTS)

os.chdir(ANDROFLEET_PATH)
subprocess.call(['pwd'])
subprocess.call(['calabash-android',
'run', 'appMock-debug.apk',
'features/node'+ node +'.feature', 
'ADB_DEVICE_ARG=192.168.49.' + str(int(node)+1),
'-f', 'pretty',
'-f', 'pretty',
'-o', 'results/node'+ node +'.txt',
'-f', 'json', '-o', 'results/node'+ node +'.json',
'-f', 'html', '-o', 'results/node'+ node +'.html'
#'&'
])
                
	

print("[", bcolors.OKGREEN ,"success", bcolors.ENDC, "]")
