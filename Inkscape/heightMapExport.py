'''
Created on 14.01.2016

@author: Kevin Gebhardt
'''
import subprocess
import sys
import inkex
import os

class heightMapExport(inkex.Effect):  
    def __init__(self):
        inkex.Effect.__init__(self)
        self.OptionParser.add_option("-p", "--path",
                                     action="store", type="string",
                                     dest="path", default="~/",
                                     help="The Directory for storing ContourLines")

        self.OptionParser.add_option("-f", "--file",
                                     action="store", type="string",
                                     dest="file", default="ScSuCountorLine",
                                     help="The Filename (no need for extensions)")

    def get_filename_parts(self):
        '''
        Attempts to get directory and image as passed in by the inkscape
        dialog. If the boolean ignore flag is set, then it will ignore
        these settings and try to use the settings from the export
        filename.
        '''
        if self.options.file == "" or self.options.file is None:
            inkex.errormsg("Please enter an image name")
            sys.exit(0)
        dirname = self.options.path
        if dirname == '' or dirname is None:
            dirname = '~/'
        dirname = os.path.expanduser(dirname)
        dirname = os.path.expandvars(dirname)
        dirname = os.path.abspath(dirname)
        if dirname[-1] != os.path.sep:
            dirname += os.path.sep
        if not os.path.isdir(dirname):
            os.makedirs(dirname)
        return dirname, self.options.file

    def effect(self):
        DEBUG = False
        #just passes everything on to java
        inputfilename = sys.argv.pop()
        outputdir, outputfilename = self.get_filename_parts()
        p = subprocess.Popen(['java', '-jar', 'HeightMapMaker.jar', inputfilename, outputdir+outputfilename+'.scl'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=subprocess.PIPE)
        (out, err) = p.communicate()
        if DEBUG:
            sys.stderr.write(repr(out))
            sys.stderr.write(repr(err))
            sys.stderr.write('     ')
            sys.stderr.write(inputfilename)
            sys.stderr.write('     ')
            sys.stderr.write(outputdir)
            sys.stderr.write(outputfilename)
            sys.stderr.write('.scl')
            sys.stderr.write('     ')
        else:
            if not(out == '' or out == None):
                sys.stderr.write(repr(out))
            if not(err == '' or err == None):
                sys.stderr.write(repr(err))
            
if __name__ == '__main__':
    e = heightMapExport()
    e.affect()

