# I AM IRON MAN!

from bs4 import BeautifulSoup
import requests, configparser, pickle, os, re

configFilePath = "config.ini"
courseFilePath = "course.ini"

def main():
    config = configure(True)
    login(config)
    register()

def configure(conf):
    config = configparser.ConfigParser()
    config.optionxform = str

    if conf == True:
        config.read(configFilePath)
    else:
        config.read(courseFilePath)
        
    return config

def login(config):
    loginUrl = config['Site Info']['login']
    session = requests.session()

    cookies = setCookies(config['Cookies'])

    r = session.get(loginUrl, cookies = cookies)

    if r.url.find("shib.idm.umd.edu") != -1:
        getNewCookies(config)
        login(configure(True))
        
    print("login performed")
    saveCookies(session)

def setCookies(c):
    cookies = {}
    for cookie in c:
        cookies[cookie] = c[cookie]

    return cookies

def saveCookies(session):
    file = "cookies.txt"
    with open(file, 'wb') as f:
        pickle.dump(session.cookies, f)

def loadCookies(session):
    global config
    file = "cookies.txt"
    with open(file, 'rb') as f:
        session.cookies.update(pickle.load(f))

def getNewCookies(config):
    for cookie in config['Cookies']:
        print('\nPlease enter cookie value of "{0}":'.format(cookie))
        config['Cookies'][cookie] = input()

    with open(configFilePath, 'w') as configfile:
        config.write(configfile)

def getSessionId():
    config = configure(True)
    url = config["Site Info"]["register"] + config["Site Info"]["term"]

    session = getSession()

    r = session.get(url)
    if r.status_code == 409:
        session.post(config["Site Info"]["signoff"])
        return getSessionId()
    elif r.status_code == 503:
        print("Sorry, you are out of operation hours...")
    elif r.status_code != 200:
        raise Exception()

    return r.json()['sessionId']

def register():
    sessionId = getSessionId()
    courses = configure(False)
    session = getSession()

    for course in courses.sections():
        courseId = course[courseId].upper()
        section = course[section]
        grading = course[grading].upper()
        credit = course[credit]

        courseInfo = {
            'course'        : courseId,
            'section'       : section,
            'gradingMethod' : grading,
            'credits'       : credit,
            'dropAddCode'   : "  "
            }

        data = {
            'courses'   : [courseInfo],
            'sessionId' : sessionId,
            'fKey'      : 'ENTER'
            }
        
        #TODO: get URL for registeration
        r = session.post(URL, data = data)

        #TODO: test if post success

def getSession():
    session = requests.session()
    loadCookies(session)

    return session
    

if __name__ == "__main__":
    main()
