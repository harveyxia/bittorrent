import subprocess
import signal
import atexit
import sys
import os

root = '../../'

def kill_child(pid):
    if pid is None:
        pass
    else:
        os.kill(pid, signal.SIGTERM)

def popen(args):
    p = subprocess.Popen(args, cwd=root, stdout=subprocess.PIPE)
    pid = p.pid
    atexit.register(kill_child, pid)
    return p

p = popen(['java', 'tracker.Tracker', '6789'])
line = p.stdout.readline()

while line:
    sys.stdout.write(line)
    line = p.stdout.readline()