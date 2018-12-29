import os

path = "C:/Users/Wojciech/IdeaProjects/holdem/src/main/java/poker/cards/icons/png"

substrings = ['_of_', 'lubs', 'iamonds', 'pades', 'earts', 'ueen', 'ing', 'ack', 'ce']
        
for filename in os.listdir(path):
    if filename[len(filename)-5] == 's':
        newfilename = filename.replace('10','T')
        for substring in substrings:
            newfilename = newfilename.replace(substring, '')
        newfilename = newfilename.replace(newfilename[0], newfilename[0].upper())
        os.rename(os.path.join(path,filename),os.path.join(path,newfilename))