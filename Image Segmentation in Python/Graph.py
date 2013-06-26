'''
CSE 417 Algorithms and computational Complexity 
1/28/13
@author: Andrew Neville 

This file maintains the graph edge and weight information and is intended 
for use with ImageSeg.py. This implementation maintains a priority queue
of vertex edges and edge weights. Weight is calculated as the Euclidean 
distance between the color values of the two pixels passed in as 
function parameters: 
Weight(vi, vj) = sqrt( (r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2 )

edges are ordered in the priority queue by edge weight values
'''

import math 
from Queue import PriorityQueue

class Graph(object):

    def __init__(self):
        self.edgeWeightPQ = PriorityQueue(-1)
    
    
    '''
    Add edge to priority queue
    '''  
    def addEdge(self,r1,g1,b1,r2,g2,b2,edge):
        self.edgeWeightPQ.put((self.weight(r1,g1,b1,r2,g2,b2), edge))
    
    
    '''
    Calculate weight of an edge
    '''
    def weight(self,r1,g1,b1,r2,g2,b2):
        return math.sqrt(((r1 - r2)**2 + (g1 - g2)**2 + (b1 - b2)**2))
    

    '''
    Display all elements in priority queue
        FOR DEBUG PURPOSES ONLY
    '''
    def display(self):
        while not self.edgeWeightPQ.empty():
            print self.edgeWeightPQ.get()
    

    '''
    Cleaner way to get edge from priority Queue
    '''
    def getEdge(self):       
        return self.edgeWeightPQ.get()
    

    '''
    Check to see if priority queue is empty
    '''
    def isEmpty(self):     
        return self.edgeWeightPQ.empty()
        
            