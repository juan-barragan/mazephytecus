#!/usr/bin/python

import Image
import sys
import os
import fnmatch
import glob

def getBits(filename):
    im = Image.open(filename)
    width = im.size[0]
    height = im.size[1]
    ans = [];
    bitmap = im.load()
    for i in range(height):
        number = 0
        for j in range(width):
            number<<=1
            bit = 0
            pixel = bitmap[j,i]
            for rgb in pixel:
                if rgb>0:
                    bit = 1
            number += bit
        ans.append(number)
    return ans

def getJavaLine(fileName, bits):
    fname = getCleanFileName(fileName)
    ans = "public static final int[] " + fname + " = { "
    for row in range(len(bits)-1):
        ans += str(bits[row]) + ","
    ans += str(bits[len(bits)-1]) + " }; \n";
    return ans

def getCleanFileName(withPathFileName):
    withPathFileName = fileName.split("/")
    [fname, ext] = withPathFileName[len(withPathFileName)-1].split(".")
    return fname

#java file to be written
fileHandle = open("./MazeTemplates.java", "w")
fileHandle.write( "package ibqpd.maze; \n")
fileHandle.write("public final class MazeTemplates { \n")

lastLine = "public static final int columns = 16;\n" + "public static final int rows = 24;\n" + "public static final int[][] templates = { "  
path = "../../../res/drawable/"
filesSorted = sorted(glob.glob(os.path.join(path, "t*.png")))
i=0
for fileName in filesSorted:
    fileHandle.write(getJavaLine(fileName, getBits(fileName)))
    lastLine += getCleanFileName(filesSorted[i])
    if(i != len(filesSorted)-1):
        lastLine += ","
    i=i+1
lastLine += " };\n"
fileHandle.write(lastLine)
fileHandle.write("}\n")
fileHandle.close()


