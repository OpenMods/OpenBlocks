import sys
from string import Template

COLORS = [
    "black",
    "red",
    "green",
    "brown",
    "blue",
    "purple",
    "cyan",
    "lightgray",
    "gray",
    "pink",
    "lime",
    "yellow",
    "lightblue",
    "magenta",
    "orange",
    "white",
]

template_elevator = Template("""{
    "parent" : "block/cube_all",
    "textures" : {
        "all" : "openblocks:blocks/elevator_$color"
    },
    "display": {
        "thirdperson": {
            "rotation": [ 10, -45, 170 ],
            "translation": [ 0, 1.5, -2.75 ],
            "scale": [ 0.375, 0.375, 0.375 ]
        }
    }
}
""")

template_elevator_rot = Template("""{
    "parent" : "block/cube_top",
    "textures" : {
        "side" : "openblocks:blocks/elevator_$color",
        "top" : "openblocks:blocks/elevator_rot_$color"
    },
    "display": {
        "thirdperson": {
            "rotation": [ 10, -45, 170 ],
            "translation": [ 0, 1.5, -2.75 ],
            "scale": [ 0.375, 0.375, 0.375 ]
        }
    }
}
""")

def build(prefix, template):
    for color in COLORS:
        with open(prefix + "_" + color + '.json', 'wb') as out:
            out.write(template.substitute(color = color))

build("elevator", template_elevator)
build("elevator_rotating", template_elevator_rot)