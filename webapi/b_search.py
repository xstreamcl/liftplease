Flipkat 12.927837,77.633021
Suncity 12.9039052,77.7069506
On way : Dominos 12.9226,77.6493602

def binarySearch(route,point):
	if len(route)==0:
		pass
	else:
		midpoint=len(route)/2
		st=vincenty(min(route),point).meters
		ed=vincenty(max(route),point).meters
		if st < ed:
			print st
			return binarySearch(route[:midpoint],point)
		else:
			print ed
			return binarySearch(route[midpoint+1:],point)
