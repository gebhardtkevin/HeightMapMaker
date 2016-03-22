#!/usr/bin/env python 
import math
import inkex
import simplepath
import sys


class heightMapSetter(inkex.Effect):
    def __init__(self):
        inkex.Effect.__init__(self)
        self.OptionParser.add_option("-l", "--height",
                                     action="store", type="float",
                                     dest="height", default=0.0,
                                     help="Adds a height to a given path")
									 
        self.OptionParser.add_option("-g", "--groundHeight",
                                     action="store", type="float",
                                     dest="groundHeight", default=0.0,
                                     help="Sets the base height")
									 
        self.OptionParser.add_option("-d", "--display",
                                     action="store", type="string",
                                     dest="display", default="all",
                                     help="What should be shown?")

    def effect(self):
        svg = self.document.getroot()
        svg.set('scsuGround', str(self.options.groundHeight))
        counter = 0
        for id, node in self.selected.iteritems():
            if node.tag == inkex.addNS('path', 'svg'):
                path_nodes = self.document.xpath("//svg:path", namespaces=inkex.NSS)
                is_used = 1
                while is_used:
                    name = str(counter) + "_CL_" + str(self.options.height)
                    for path_node in path_nodes:
                        if path_node.get("id") == name:
                            counter += 1
                            break
                    else:
                        is_used = 0
                node.set('id', name)
                counter += 1
        for node in self.document.xpath("//svg:path", namespaces=inkex.NSS):
            if self.options.display == 'cl':
                    if node.get('id'):
                        if node.get('id').find('_CL_') == -1:  # isNoCL
                            node.set('scsuHidden', 'true')
                            node.set('display', 'none')
                        elif node.get('scsuHidden') == 'true':
                            node.set('scsuHidden', 'false')
                            node.set('display', 'true')
            elif self.options.display == 'nocl':
                    if node.get('id'):
                        if node.get('id').find('_CL_') != -1:  # isCL
                            node.set('scsuHidden', 'true')
                            node.set('display', 'none')
                        elif node.get('scsuHidden') == 'true':
                            node.set('scsuHidden', 'false')
                            node.set('display', 'true')
            elif self.options.display == 'all':
                    if node.get('scsuHidden') == 'true':
                        node.set('scsuHidden', 'false')
                        node.set('display', 'true')

if __name__ == '__main__':
    e = heightMapSetter()
    e.affect()