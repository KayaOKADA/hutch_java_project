import sys,math
import numpy as np
from sklearn.cluster import KMeans

def main():

    # File input
    datafile = open(sys.argv[1] + "clusteringInput.csv", "r")
    numc = -1
    numline = 1
    numdim = 1
    nline = 0
    for line in datafile:
        ndim = 0
        s = line[:-1].split(',')
        if (numc < 0):
            numc = int(s[0])
            numline = int(s[1])
            numdim = int(s[2])
            fdata =  [[0 for i in range(numdim)] for j in range(numline)]
            continue
        for v in s:
            fdata[nline][ndim] = float(v)
            ndim = ndim + 1
        nline = nline + 1

    datafile.close()
    #print(str(numc) + " " + str(numline) + " " + str(numdim))
    
    # Invoke K-means
    features = np.array(fdata)
    #print(features)
    km = KMeans(n_clusters=numc).fit(features)
    labels = km.labels_
    print(labels)
    
    # File output
    labelfile = open(sys.argv[1] + "clusteringOutput.csv", "w")
    for label in labels:
        labelfile.write(str(label) + "\n")
    labelfile.close()


# call the main function
if __name__ == '__main__':
    main()    
