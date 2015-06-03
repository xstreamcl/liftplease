from polyline import GPolyCoder as gpe
import math


def haversine(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points
    on the earth (specified in decimal degrees)
    """
    # convert decimal degrees to radians
    lon1, lat1, lon2, lat2 = map(math.radians, [lon1, lat1, lon2, lat2])
    # haversine formula
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
    c = 2 * math.asin(math.sqrt(a))
    km = 6367 * c
    return 1000*km


def calculate_min_dist(route, point):
    path = gpe().decode(route)
    mindist = 99999999
    for points in path:
        curmin = haversine(points[1],points[0],point[1],point[0])
        if curmin < mindist:
            mindist = curmin
    return mindist
