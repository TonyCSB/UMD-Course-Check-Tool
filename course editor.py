import configparser, requests
from bs4 import BeautifulSoup
from prettytable import PrettyTable

courseFilePath = "course.ini"
config = configparser.ConfigParser()
config.optionxform = str
config.read(courseFilePath)


def main():
    print("Welcome to course index editor         - By Tony")
    while True:
        choice = printIntro()
        select(choice)

def printIntro():
    print("\nPlease enter numbers to indicate your choice:")
    print("1. Print your existing list")
    print("2. Add another course/section")
    print("3. Remove course/section")
    print("4. Exit the program\n")

    while True:
        try:
            choice = input()
            if 1 <= int(choice) <= 4:
                choice = int(choice)
                return choice
            
        except:
            pass

        print("Wrong format, pleace reenter: ")

def select(choice):
    print()
    
    if choice == 1:
        printList()
    elif choice == 2:
        addItem()
    elif choice == 3:
        removeItem()
    else:
        exit()

def printList():
    i = 1
    x = PrettyTable()

    x.field_names = ['Priority',
                     'Course ID',
                     'Section ID',
                     'Grading Method',
                     'Credits',
                     'Waitlist?'
                     ]
    
    while str(i) in config.sections():
        
        x.add_row([i,
                   config[str(i)]['courseId'],
                   config[str(i)]['section'],
                   config[str(i)]['grading'],
                   config[str(i)]['credit'],
                   config[str(i)].getboolean('waitlist')
                   ])
        i = i + 1

    print()
    print(x)

def addItem():
    print("Please enter the course id (in the form of 'ABCD123'): ")
    while True:
        courseId = input().upper()
        if checkCourseId(courseId):
            break
        print("Wrong format, pleace reenter: ")

    print("\nPlease enter the section id (in the form of '0101', separated by space): ")
    while True:
        sectionId = input()
        sectionList = sectionId.split(" ")
        result = True
        for sectionId in sectionList:
            if not checkSectionId(sectionId):
                result = False
        if result == True:
            break
        print("Wrong format, pleace reenter: ")

    print("\nPlease enter the grading method (in the form of 'R'): ")
    while True:
        grading = input().upper()[0]
        if grading.isalpha():
            break
        print("Wrong format, pleace reenter: ")

    print("\nPlease enter the credits: ")
    while True:
        credit = input()
        if credit.isdigit():
            break
        print("Wrong format, pleace reenter: ")

    printList()
    print("\nPlease enter the place you would like to put this course/section after: ")
    while True:
        place = input()
        if place.isdigit() and 0 <= int(place) <= len(config.sections()):
            break
        print("Wrong format, pleace reenter: ")

    sections = {}
    for section in sectionList:
        if(fullClass(courseId, section)):
            print("The section {0} of course {1} is full or almost full (less than 10%), do you wish to (R)emove this section or (E)nable waitlist function?".format(section, courseId))
            while True:
                choice = input().lower
                if choice[0] == "e":
                    sections[section] = 1
                    break
                elif choice[0] == "r":
                    break
                else:
                    print("Wrong format, pleace reenter: ")

        else:
            sections[section] = 0

    i = len(config.sections())

    for a in range(len(sections)):
        config.add_section(str(i + a + 1))
        
    while i != int(place):
        config[str(i + len(sectionList))]['courseId'] = config[str(i)]['courseId']
        config[str(i + len(sectionList))]['section'] = config[str(i)]['section']
        config[str(i + len(sectionList))]['grading'] = config[str(i)]['grading']
        config[str(i + len(sectionList))]['credit'] = config[str(i)]['credit']
        config[str(i + len(sectionList))]['waitlist'] = config[str(i)]['waitlist']

        i = i - 1

    i = 1
    for section in sections:
        config[str(int(place) + i)]['courseId'] = courseId
        config[str(int(place) + i)]['section'] = section
        config[str(int(place) + i)]['grading'] = grading
        config[str(int(place) + i)]['credit'] = credit
        config[str(int(place) + i)]['waitlist'] = str(sections[section])

        i = i + 1

    with open(courseFilePath, 'w') as courseFile:
        config.write(courseFile)

def removeItem():
    if len(config.sections()) == 0:
        print("There is no element to be removed!")
        return False

    printList()
    print("\nPlease enter the number of section to be removed (separated by space): ")
    while True:
        remove = input()
        removeList = remove.split(" ")
        result = True
        for remove in removeList:
            if (not remove.isdigit()) or int(remove) <= 0 or int(remove) > len(config.sections()):
                result = False
        if result == True:
            break

    dis = 0
    for i in range(len(config.sections())):
        if str(i + 1) in removeList:
            dis = dis + 1
        else:
            config[str(i + 1 - dis)]['courseId'] = config[str(i + 1)]['courseId']
            config[str(i + 1 - dis)]['section'] = config[str(i + 1)]['section']
            config[str(i + 1 - dis)]['grading'] = config[str(i + 1)]['grading']
            config[str(i + 1 - dis)]['credit'] = config[str(i + 1)]['credit']
            config[str(i + 1 - dis)]['waitlist'] = config[str(i + 1)]['waitlist']

    length = len(config.sections())
    for i in range(len(removeList)):
        config.remove_section(str(length - i))

    print("Job Done!\n")
    with open(courseFilePath, 'w') as courseFile:
        config.write(courseFile)

def checkCourseId(courseId):
    if len(courseId) < 7:
        return False

    if not courseId[:4].isalpha():
        return False
    if not courseId[4:7].isdigit():
        return False

    return True

def checkSectionId(sectionId):
    if len(sectionId) != 4:
        return False

    if not sectionId.isdigit():
        return False

    return True

def fullClass(courseId, section):
    url = "https://app.testudo.umd.edu/soc/search?courseId={0}&sectionId={1}&termId=201908&_openSectionsOnly=on&creditCompare=&credits=&courseLevelFilter=ALL&instructor=&_facetoface=on&_blended=on&_online=on&courseStartCompare=&courseStartHour=&courseStartMin=&courseStartAM=&courseEndHour=&courseEndMin=&courseEndAM=&teachingCenter=ALL&_classDay1=on&_classDay2=on&_classDay3=on&_classDay4=on&_classDay5=on".format(courseId, section)
    r = requests.get(url)
    soup = BeautifulSoup(r.text, features = 'html.parser')
    openSeat = int(soup.find('span', {'class':'open-seats-count'}).string)
    totalSeat = int(soup.find('span', {'class':'total-seats-count'}).string)

    if openSeat / totalSeat <= 0.1:
        return True

    return False
    

if __name__ == "__main__":
    main()
