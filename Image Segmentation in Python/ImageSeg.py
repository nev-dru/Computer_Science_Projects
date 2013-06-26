'''
CSE 417 Algorithms and computational Complexity 
1/28/13
@author: Andrew Neville 

Image Segmentation with Minimum Spanning Forests. 
An implementation of Kruskal's algorithm, with a provision for stopping when the number of trees
in the spanning forest reaches the value of a parameter ntrees. Image data input is in the form
of a text file which contains a list of lists representing the RGB values of each pixel in the image. 
A graph is constructed where there is one vertex for each pixel. There is an edge between two 
vertices if their pixels are adjacent (share a pixel boundary) in the image. Each pixel has an RGB 
triple (r,g,b) associated with it which is in the list of lists. The weight of an 
edge is computed as the Euclidean distance between the color values of the two pixels involved: 
D(vi, vj) = sqrt( (r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2 )
Because the image data has a regular structure to it, the graph representation is array based.

The output results of image segmentation is also a list of lists of the form below.
[[0, 0, 0, 1], [2, 0, 1, 1], [2, 2, 1, 1]]
In this example, the image is 3 rows by 4 columns. The value of ntrees is 3, and so the pixels are 
labeled with values in the range {0, 1, 2}. The numbers of rows and columns will depend on the input data.

This program also implements the UNION/FIND ADT using uptrees and path compression, At the end 
of a run of Kruskal's algorithm, the number of UNION/FIND operations is reported. The results 
of path compression before and after are displayed on the console. 

The program will open and read the specified file to get the image data. The image data 
will be in the form of a list of lists of triples of integers. 

Here is a sample of the file contents for a very small image that could be the input for 
the sample output given above
[[(50, 50, 50), (50, 50, 50), (50, 50, 50), (79, 20, 16)],
 [(32, 93, 78), (50, 50, 50), (78, 21, 17), (79, 20, 16)],
 [(31, 89, 78), (33, 90, 77), (79, 21, 15), (77, 20, 17)]]

the output is written to the console and a text file

'''


import Graph

'''
Main method
'''
def main():
    
    #output files
    basicResult = "BASIC-RESULT.txt"
    earthResult = "EARTH-RESULTS.txt"

    #input files
    testFile = "test.txt"
    earthFile = "earth-sm2.txt"
    
    #Set Program in/out Data
    inputFile = testFile
    outFile = file(basicResult, 'w')
    ntrees = 3 #number of mst's
    
   
    
    print 'ntrees = %d' %(ntrees)
    #create Graph
    [pixGraph, pixImg] = imgGraph(inputFile)
    npixImg = nKruskal(ntrees, pixGraph, pixImg)

    print 'uptree before path compression: ',pixImg
    segmentaiton = imageSeg(npixImg)
    print 'Image segmentation: ', segmentaiton
    print >> outFile, segmentaiton
    
    outFile.close()
    return    


'''
Kruskal's algorithm, with a provision for 
stopping when the number of trees in 
the spanning forest reaches a specified value
'''
def nKruskal(ntrees, pixGraph, pixImg):
    r =  len(pixImg) #rows
    c = len(pixImg[0]) #cols
    totalTrees = r*c
    steps = 0
    #for each (u, v) ordered by weight(u, v), increasing:
    while not pixGraph.isEmpty():
        edge = pixGraph.getEdge()
        t1, t2 = edge[1]
        t11, t12 = t1 #index of first vertex
        t21, t22 = t2 #index of second vertex
        #get roots and compress path
        root1,steps1 = PCFind(pixImg,t11,t12)
        root2,steps2 = PCFind(pixImg, t21, t22) 
        steps = steps + steps1 + steps2
        if root1 != root2:
            #if we already have n trees
            if totalTrees == ntrees:
                break
            else:
                #union
                n1,n2 = root2
                pixImg[n1][n2] = root1
                totalTrees = totalTrees - 1
    print 'Total steps taken by Path Compression Find: ', steps
    return pixImg


'''
Find operation with no path compression
    finds the root of the vertex in the image
'''
def find(pixImg,t1,t2, steps):
    steps = steps + 1
    n1,n2 = pixImg[t1][t2]
    if [n1, n2] == [t1, t2]:
        return [pixImg[n1][n2], steps]
    else: 
        return find(pixImg,n1,n2,steps)


'''
Find operation with path compression
    finds the root of the vertex in the image
'''
def PCFind(pixImg,t1,t2):
    #find root
    r, steps = find(pixImg,t1,t2,0) #find root
    r1,r2 = r
    if [t1,t2] != [r1,r2]: #compress
        k1,k2 = pixImg[t1][t2] 
        pixImg[t1][t2] = (r1,r2)
        while [k1,k2] != [r1,r2]:
            t1,t2 = pixImg[k1][k2] 
            pixImg[k1][k2] = (r1,r2)
            k1,k2 = pixImg[t1][t2]
    return [pixImg[r1][r2], steps]


'''
Method that takes in an image from a text file format
and constructs a Priority Queue of edges based on the pixel RGB values
D(vi, vj) = sqrt( (r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2 )
'''
def imgGraph(fname):
    f = open(fname, "r")
    img = eval(f.read())
    pixGraph = Graph.Graph() #create graph
    r = len(img) #rows
    c = len(img[0]) #cols
    pixImg = [[None for j in range(c)] for i in range(r)] #empty matrix for pixel values
    for i in range(0,r):
        for j in range(0,c):
            R1, G1, B1 = img[i][j]
            if j != 0:
                R2, G2, B2 = img[i][j-1]
                e1 = ((i,j),(i,j-1)) #edge between pixel vertices
                pixImg[i][j-1] = e1[1] #pixel vertex
                pixImg[i][j] = e1[0] #pixel vertex
                pixGraph.addEdge(R1,G1,B1,R2,G2,B2,e1)
            if i != 0:
                R2, G2, B2 = img[i-1][j]
                e2 = ((i,j),(i-1,j)) #edge between pixel vertices
                pixImg[i-1][j] = e2[1] #pixel vertex
                pixImg[i][j] = e2[0] #pixel vertex
                pixGraph.addEdge(R1,G1,B1,R2,G2,B2,e2)
    return [pixGraph, pixImg]
    
            
'''
Image segmentation output
    outputs the segments of the image based on the specified 
    number of preferred trees. 
'''
def imageSeg(pixImg):
    roots = {} #dictionary of roots
    rootCount = 0
    r =  len(pixImg) #rows
    c = len(pixImg[0]) #cols
    segImg = [[None for j in range(c)] for i in range(r)]
    for i in range(r):
        for j in range(c):
            root,steps = PCFind(pixImg,i,j) #compresses all paths
            if not roots.has_key(root):
                roots[root] = rootCount
                rootCount = rootCount + 1
            segImg[i][j] = roots[root]
    print 'uptree after path compression: ',pixImg
    return segImg
 
if __name__ == '__main__':
    main()