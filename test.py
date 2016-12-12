import socket
import numpy as np
import cv2
from pylepton import Lepton
import sys, json
import gzip
import StringIO

host = "172.30.92.215"
port = 1024


with Lepton() as l:
	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	s.connect((host, port))

	while True:
		a,_ = l.capture()
		cv2.normalize(a, a, 0, 65535, cv2.NORM_MINMAX) # extend contrast
		np.right_shift(a, 8, a) # fit data into 8 bits
		np.set_printoptions(threshold=np.prod(a.shape))
		#print a
		s.sendall(str(a).replace('\n','').replace(' ',''))
		print "sent"
		s.sendall('\n')
		print "newline"
	d.close
