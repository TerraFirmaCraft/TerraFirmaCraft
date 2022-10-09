# Import the json file
json = {}
true = True
false = False
with open('/../../../../Beautiful Artwork/TFC Plants/00_TFC Animals/2022/.Animations/animation.json', 'r') as file:
    text = file.read()
    file.close()
    json = eval(text)

# extract the relevant information
anim_dict = json['animations']
anim_name = list(anim_dict.keys())[0]
animation = anim_dict[anim_name]
bones = animation['bones']

# Get some parameters for java
# Rounding the duration to the nearest tick
name = anim_name.split('.')[-1].upper()
naked_duration = animation['animation_length']
duration = round(20 * naked_duration) / 20

# Now we start building the code
code = 'public static final AnimationDefinition %s = AnimationDefinition.Builder.withLength(%s)\n' % (name, str(duration) + 'F')

for i in bones:
    # a_name is the name of the part moving
    a_name = i
    # We're assuming the animation is linear. You can change this if you want I guess.
    code = code + '.addAnimation(\"%s\", new AnimationChannel(AnimationChannel.Targets.ROTATION' % a_name

    # The animation timestamps
    times = bones[i]['rotation']

    # This tracks when we're on the last step so that we can use the rounded value
    t = 0
    for time in times:
        t += 1
        # Interpret the string as an integer
        if t == len(times):
            timestamp = duration
        else:
            timestamp = eval(time)

        # Rotation parameters
        params = times[time]
        if params[0] == params[1] == params[2] == 0:
            code = code + ', noRotation(%sF)' % timestamp
        else:
            code = code + ', rotation(%sF, %sF, %sF, %sF)' % (timestamp, params[0], params[1], params[2])
    code = code + '))\n'

code = code + '.build();'

print('\n\n')
print(code)
input()
