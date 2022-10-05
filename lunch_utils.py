#!/usr/bin/env python3
from unittest import result
import requests
from bs4 import BeautifulSoup
from datetime import date
from itertools import islice

def chunks(lst, n):
    """Yield successive n-sized chunks from lst."""
    for i in range(0, len(lst), n):
        yield lst[i:i + n]

# https://www.pokhara.cz/
# http://www.krusovickaomega.cz/cz/tydenni-menu
# https://www.skanzeen.cz/
# https://pizzeriebasilico.cz/tydenni-menu/

#Monday is 0 and Sunday is 6
today = date.today().weekday()
if(today>4):
  today = 0

def getTusto(URL):
  page = requests.get(URL)
  soup = BeautifulSoup(page.content, "html.parser")
  soap = soup.find('div', class_='soap')
  results = []
  splitSoup = soap.text.strip().split()
  if(len(splitSoup) > 4):
    results.append({"text": splitSoup[1:3], "price": ''})
  elements = soup.find_all("ol", class_="weekly-list")
  for element in elements:
    texts = element.find_all('span', class_="str")
    prices = element.find_all('em')
    for index, item in enumerate(texts):
      results.append({"text": item.text.strip(), "price": prices[index].text.strip()})
  return results

def getPokhara(URL):  
  page = requests.get(URL)
  soup = BeautifulSoup(page.content, "html.parser")
  elements = soup.find_all("p")[6:]
  currentChunk = list(chunks(elements, 8))[today][1:]
  results = []
  for element in currentChunk:
    elementContent = element.text.strip()
    text = " ".join(elementContent.split()[:-1])
    price = elementContent.split()[-1]
    results.append({"text": text, "price": price})
  return results


def getOmega(URL):
  page = requests.get(URL)
  soup = BeautifulSoup(page.content, "html.parser")
  mainPage = soup.find(id="hlavni_oblast")
  elements = mainPage.find_all("p")[5:]
  price = elements[-1].text.strip().split()[-2]
  
  currentChunk = list(chunks(elements, 6))[today][1:]
  results = []
  for index, chunk in enumerate(currentChunk):
    if(index == 0):
      results.append({"text": chunk.text.strip(), "price": ''})
    if(index != (len(currentChunk) - 1) and index != 0):
      results.append({"text": chunk.text.strip(), "price": price})
  
  last = currentChunk[len(currentChunk)-1].text.strip()
  lastText = " ".join(last.split()[:-2])
  lastPrice = last.split()[-2]
  results.append({"text": lastText, "price": lastPrice})
  return results

def getSkanzeen(URL):
  page = requests.get(URL)
  soup = BeautifulSoup(page.content, "html.parser")
  elements = soup.find("div", class_="hp-denni-menu-item")
  texts = elements.find_all("div", class_="text-block-7")
  prices = elements.find_all("div", class_="text-block-6")
  results = []
  for index, text in enumerate(texts):
    results.append({"text": text.text.strip(), "price": prices[index].text.strip()})
  return results

def getBasilico(URL):
  page = requests.get(URL)
  soup = BeautifulSoup(page.content, "html.parser")
  tableItems = soup.find_all("tr")
  itemChunks = list(chunks(tableItems[10:], 7))
  allChunks = ([tableItems[2:9]] + itemChunks)
  result = []
  for chunk in allChunks[today][:-1]:
    text = chunk.text.strip()
    splitText = text.split('\n')
    if(len(splitText) > 1 and splitText[0]):
      result.append({"text": splitText[0], "price": splitText[1]})
  
  return result
  
def getLunch(restaurant_name, URL):
  if (restaurant_name == 'tusto'):
    return getTusto(URL)
  elif (restaurant_name == 'pokhara'):
    return getPokhara(URL)
  elif (restaurant_name == 'omega'):
    return getOmega(URL)
  elif (restaurant_name == 'skanzeen'):
    return getSkanzeen(URL)
  if (restaurant_name == 'basilico'):
    return getBasilico(URL)

if __name__ == "__main__":
  print(getOmega('http://www.krusovickaomega.cz/cz/tydenni-menu'))
  # print(getTusto('https://titanium.tusto.cz/home/#menu'))