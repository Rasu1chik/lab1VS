#!/usr/bin/env python3
"""End-to-end checks via Gateway. Uses only Python standard library; run after compose up.
Creates isolated demo records. --resilience temporarily stops and restarts manager-service.
"""
import argparse,json,time,uuid,urllib.request,urllib.error,subprocess
from pathlib import Path
parser=argparse.ArgumentParser();parser.add_argument('--base-url',default='http://localhost:8080');parser.add_argument('--resilience',action='store_true');args=parser.parse_args()
base=args.base_url.rstrip('/')+'/api/v1';suffix=uuid.uuid4().hex[:8]
def request(method,path,data=None,expect=200):
 req=urllib.request.Request(base+path,data=None if data is None else json.dumps(data).encode(),headers={'Content-Type':'application/json'},method=method)
 try:
  with urllib.request.urlopen(req,timeout=12) as response:status=response.status;headers=response.headers;body=response.read()
 except urllib.error.HTTPError as e:status=e.code;headers=e.headers;body=e.read()
 assert status==expect,f'{method} {path}: expected {expect}, got {status}: {body.decode()}'
 return (json.loads(body) if body else None),headers

def create(path,data):
 result,headers=request('POST',path,data,201);assert 'Location' in headers;return result
for i in range(90):
 try:request('GET','/clubs?size=1');break
 except (AssertionError,OSError):time.sleep(2)
else:raise RuntimeError('Gateway or services not ready')
manager=create('/managers',{'username':'owner'+suffix,'email':suffix+'@example.org'});mid=manager['id']
request('GET',f'/managers/{mid}');request('GET','/managers?size=51',expect=400)
invalid,_=request('POST','/managers',{'username':'','email':'bad'},400);assert invalid['errors']
request('POST','/managers',{'username':'duplicate'+suffix,'email':suffix+'@example.org'},409)
request('PUT',f'/managers/{mid}',{'username':'renamed'+suffix,'email':suffix+'@example.org'})
_,headers=request('GET','/managers?size=1');assert int(headers['X-Total-Count'])>=1
market,headers=request('GET','/players?name=haaland&size=10');assert market['content'][0]['lastName']=='Haaland';assert 'X-Total-Count' not in headers
request('GET','/players?size=51',expect=400)
first,_=request('GET','/players?size=15')
assert {p['firstName']+' '+p['lastName'] for p in first['content']}=={
 'David Raya Martín','Kepa Arrizabalaga Revuelta','William Saliba','Virgil van Dijk','Joe Gomez','Jeremie Frimpong',
 'Levi Samuels Colwill','Enzo Fernández','Moisés Caicedo Corozo','Phil Foden','Rayan Cherki','Tijjani Reijnders',
 'Yoane Wissa','William Osula','Harvey Barnes'}
assert market['content'][0]['price']==15.6
# Original CRUD deletion scenario for independent player and club.
club=create('/clubs',{'name':'Temporary '+suffix,'shortName':suffix[-3:],'country':'England'})
player=create('/players',{'clubId':club['id'],'firstName':'Alex','lastName':'Smith','position':'MID','status':'AVAILABLE','price':5})
changed,_=request('PUT',f"/players/{player['id']}",dict(player,lastName='Brown',price=6));assert changed['lastName']=='Brown'
request('DELETE',f"/players/{player['id']}",expect=204);request('DELETE',f"/clubs/{club['id']}",expect=204)
print('PASS: R2DBC manager CRUD, validation, unique constraints; catalog search and pagination')
# Five separate clubs; every three players from the same club.
clubs=[]
for i in range(5):
 clubs.append(create('/clubs',{'name':'Smoke '+suffix+str(i),'shortName':suffix[:2]+str(i),'country':'England'}))
positions=['GK']*2+['DEF']*5+['MID']*5+['FWD']*3
roster=[]
for i,pos in enumerate(positions):
 roster.append(create('/players',{'clubId':clubs[i//3]['id'],'firstName':'Smoke','lastName':suffix+str(i),'position':pos,'status':'AVAILABLE','price':5.0}))
ids=[p['id'] for p in roster];starters=ids[:11]
team_input={'managerId':mid,'name':'Smoke team','playerIds':ids,'starterIds':starters,'captainId':ids[1]}
request('POST','/fantasy-teams',dict(team_input,managerId=9223372036854775807),404)
team=create('/fantasy-teams',team_input);tid=team['id'];assert len(team['squad'])==15 and team['budget']==25
request('DELETE',f'/managers/{mid}',expect=409)
request('PUT',f'/fantasy-teams/{tid}/lineup',{'starterIds':starters,'captainId':ids[1]})
request('PATCH',f'/fantasy-teams/{tid}',{'name':'Renamed team'})
incoming=create('/players',{'clubId':clubs[0]['id'],'firstName':'New','lastName':suffix,'position':'GK','status':'AVAILABLE','price':6})
transfer=create(f'/fantasy-teams/{tid}/transfers',{'playerOutId':ids[0],'playerInId':incoming['id']});assert transfer['playerInId']==incoming['id']
request('POST',f'/fantasy-teams/{tid}/transfers',{'playerOutId':ids[1],'playerInId':ids[2]},422)
after,_=request('GET',f'/fantasy-teams/{tid}');history,_=request('GET',f'/fantasy-teams/{tid}/transfers');assert after['budget']==24 and len(history['content'])==1
# Insufficient budget leaves roster, budget and history unchanged.
for p in roster:
 request('PUT',f"/players/{p['id']}",dict(p,price=6.5))
tight=create('/fantasy-teams',dict(team_input,name='Tight budget'));assert tight['budget']==2.5
expensive=create('/players',{'clubId':clubs[0]['id'],'firstName':'Expensive','lastName':suffix,'position':'GK','status':'AVAILABLE','price':15})
request('POST',f"/fantasy-teams/{tight['id']}/transfers",{'playerOutId':ids[0],'playerInId':expensive['id']},422)
unchanged,_=request('GET',f"/fantasy-teams/{tight['id']}");history,_=request('GET',f"/fantasy-teams/{tight['id']}/transfers")
assert unchanged['budget']==2.5 and ids[0] in [p['playerId'] for p in unchanged['squad']] and expensive['id'] not in [p['playerId'] for p in unchanged['squad']] and history['content']==[]
print('PASS: Feign manager lookup, missing manager 404, team validation, transfers and rollback')
league=create('/leagues',{'ownerId':mid,'name':'League '+suffix,'type':'PRIVATE','joinCode':suffix});lid=league['id']
request('POST',f'/leagues/{lid}/teams',{'teamId':tid,'joinCode':'wrong'},422)
request('POST',f'/leagues/{lid}/teams',{'teamId':tid,'joinCode':suffix},204)
# Exercise original regression: scoring more than one page of teams.
for i in range(49):create('/fantasy-teams',dict(team_input,name='Batch '+str(i)))
_,h=request('GET',f'/fantasy-teams?managerId={mid}&size=50');assert h['X-Total-Count']=='51'
week=create('/gameweeks',{'number':int(time.time()),'deadline':'2035-01-01T12:00:00Z'});wid=week['id']
request('POST',f'/gameweeks/{wid}/start')
request('PUT',f'/gameweeks/{wid}/performances',{'playerId':ids[1],'minutesPlayed':90,'goals':1,'assists':0,'cleanSheet':True,'yellowCards':0,'redCards':0})
result,_=request('POST',f'/gameweeks/{wid}/finish');assert result['status']=='FINISHED'
request('POST',f'/gameweeks/{wid}/finish',expect=409)
page0,_=request('GET',f'/fantasy-teams?managerId={mid}&size=50');page1,_=request('GET',f'/fantasy-teams?managerId={mid}&size=50&page=1')
assert len(page0)==50 and len(page1)==1 and all(t['totalPoints']==24 for t in page0+page1)
standings,h=request('GET',f'/leagues/{lid}/standings');assert h['X-Total-Count']=='1' and standings[0]['totalPoints']==24
request('DELETE',f'/leagues/{lid}/teams/{tid}',expect=204)
spare=create('/managers',{'username':'spare'+suffix,'email':'spare'+suffix+'@example.org'})
request('DELETE',f"/managers/{spare['id']}",expect=204);request('GET',f"/managers/{spare['id']}",expect=404)
print('PASS: leagues, captain double points, all 51 teams scored, repeated finish rejected, reverse Feign reference check')
if args.resilience:
 root=Path(__file__).resolve().parents[1]
 def compose(*cmd):subprocess.run(['docker','compose',*cmd],cwd=root,check=True)
 compose('stop','manager-service')
 try:
  for i in range(5):request('POST','/fantasy-teams',team_input,503)
  request('GET',f'/fantasy-teams/{tid}')
  metrics=json.loads(subprocess.check_output(['docker','compose','exec','-T','fantasy-service','curl','-fsS','http://localhost:8082/actuator/circuitbreakers'],cwd=root))
  assert metrics['circuitBreakers']['ManagerClientgetLong']['state']=='OPEN',metrics
  print('PASS: actual Feign Circuit Breaker is OPEN')
  print('PASS: manager outage -> HTTP 503, no fake manager; independent reads remain available')
 finally:compose('start','manager-service')
 for i in range(90):
  try:
   # Uses the actual Feign circuit, and waits for half-open + Eureka recovery.
   create('/leagues',{'ownerId':mid,'name':'Recovery '+suffix,'type':'PUBLIC'});break
  except (AssertionError,OSError):time.sleep(2)
 else:raise RuntimeError('Feign did not recover')
 create('/leagues',{'ownerId':mid,'name':'Second recovery probe '+suffix,'type':'PUBLIC'})
 metrics=json.loads(subprocess.check_output(['docker','compose','exec','-T','fantasy-service','curl','-fsS','http://localhost:8082/actuator/circuitbreakers'],cwd=root))
 assert metrics['circuitBreakers']['ManagerClientgetLong']['state']=='CLOSED',metrics
 print('PASS: Feign circuit recovered to CLOSED after manager-service restart')
print('ALL END-TO-END CHECKS PASSED')
