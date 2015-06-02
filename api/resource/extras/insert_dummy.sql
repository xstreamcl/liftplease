import sqlite3
con=sqlite3.connect('./resource/lp.db')
a=con.cursor()
a.execute("insert into lp_user values (\
1,'dev122','gididid38','apppdididi','9938381928','nameanirudh','2'\
,'emasdfasdfail@fuckeasra.com','http://mysexy.photo.xx.coma1d091233=xx~!@#asdfk.c'\
,'about me thea sdbiasgdgest lunndd huge bay','org=flipkart','soft dev')")
con.commit()
a.execute("select * from lp_user")
a.fetchall()

a=con.execute("insert into lp_user values (\
3,'app_iadsdddkjad',98475643265654,'anirudha232sdfas','male'\
,'emasdfas323dfail@fuckeasra.com','http://mysexy.phot2332o.xx.coma1d091233=xx~!@#asdfk.c'\
,'about me thea sdbiasgdgest lunnddasldk huge bay','fuckiksjdfngaa occup','org-tpyte','name of factory'\
,'titke ofmanager','deptydadm')")
con.commit()
a.execute("select * from lp_user")
a.fetchall()

a=con.execute("insert into lp_user values (\
4,'app_iadsdasdfaddkjad',9847564323423265654,'anirudha2asd3a2sdfas','female'\
,'emasdfas323dlaksdfail@fuckeasra.com','http://mysexyas3.phot2332o.xx.coma1d091233=xx~!@#asdfk.c'\
,'about me thea sdbiasgdgest lunnddasldk huge bay','fuckiksjdfngaa occup','org-tpyte','name of factory'\
,'titke ofmanager','deptydadm')")
con.commit()
a.execute("select * from lp_user")
a.fetchall()

a=con.execute("insert into lp_user values (\
5,'app_iadsdasdfaddiuaikjdkjad',9847564323420394093265654,'anirudh98923a2asd3a2sdfas','female'\
,'emasdfas32aidj3dlaksdfail@fuckeasra.com','http://mysexksdyas3.phot2332o.xx.coma1d091233=xx~!@#asdfk.c'\
,'about me thea sdbiasgdgest lunnddasldk huge bay','fuckiksjdfngaa occup','org-tpyte','name of factory'\
,'titke ofmanager','deptydadm')")
con.commit()
a.execute("select * from lp_user")
a.fetchall()

a.execute("insert into lp_user values (\
6,'app_iaaddsdasdfaddiuaikjdkjad',9847564332323420394093265654,'anirudh98922323a2asd3a2sdfas','female'\
,'emasdfas32aidj3dlaksdfail@fucke23asra.com','http://mysexksdsd3yas3.phot2332o.xx.coma1d091233=xx~!@#asdfk.c'\
,'about me thea sdbiasgdgest lunnddasldk huge bay','fuckiksjdfngaa occup','org-tpyte','name of factory'\
,'titke ofmanager','deptydadm')")
con.commit()

a.execute("insert into lp_provider values (1,1433099206.961025,1010,\
'wqdnAyh{xM_HNoAB?MGuBCuDIaDS}MAqEbBErACpAKrAKzAQlKoAjAIpBKFu@b@}El@oEh@wDTuAVkCj@qHn@}KzAuUn@aK?o@~G_\
EbCaBxFkDxAu@fDgBdAm@bAw@rAoAz@m@vA{@v@Yv@IbB?`@?VEvB?f@eIDgFPqINgENqBVwB~@{Cz@iCrAmDbI_T~AqERw@JaA\
?kAQkHwBmb@s@wHqA}LqAqMaCmVY_CSmCD_C@YTqE?[NsDj@}PXmKXmJJkBF}@\\kLLoDb@uLDm@RwN@sDMwNKyUM}Ra@eNG}C\
W{D_@mE}AoQ[eDAeABiDBaD@yI?]FoG?uFBw@j@sDF_@@c@ASIWIS]g@m@s@]SmC_AcA]kD}AkIoDu@[OCeAEk@G_B[wHeBeMoCwCo@wGgAgCYeDY@I')")

a.execute("insert into lp_provider values (2,1433099206.961025,2020,\
'qwenAi{lxMH@`AJKfB?d@DdAC~B?VT@fABJBJHDPV`CdBPrAPPJv@[f@o@Xs@zAuBh@g@\\U`A_@jM{BhAQP?RD|B|@`@Sn@gALSHC`@@hA\
A^GzAi@ZKJuBAwAGmDKoDhAAf@?TWv@?nCNnADpA@hD@zABt@XdBAKeBe@qOOsDc@kCEu@Ai@Dc@Pq@dBgFLy@Hg@Je@j@Vf@P|@ArA[|@O`AMzAM~@\
AXg@TOfFaCv@s@^a@b@u@fB}CnAgCtAoCj@gA~AwCnA}@nBeBb@w@l@gBHa@|@yD`@eAh@FxDXl@W~@i@v@{@tA_Bh@k@Zk@xCyLp@mC@]TaAAAAC\
AC@IHQBAZy@h@iBj@sAbAmAbAcAv@q@nCwApAe@p@[vAm@fBm@t@Wp@[lA}@z@_A`AmAxA}BfBqC\\]r@UTCxAShEi@lLq@dE[xA_@xB{@pAs@bAw@r@\
w@^i@h@gAd@oAV_B^oEB[ZkF~@uLZ_FNcATqAV}@VaA`@oArAqC|@_BrAwB`@u@bAoAnBsCzD}FxCcERGl@{@P[v@a@tCuARWtAw@XSPWLWL_AxCkKjB\
oHbDyJQEjBgGl@uBl@kBuCw@oCq@tCyI~@uCeEaA')")

a.execute("insert into lp_provider values (3,1433099793.884708,3030,\
'{n|mAed`yMhGn@hGj@b@@?FB`@DLJ?dAKVALBBDBPdAOt@CrA@jD@xA@`EFfB@`B@rBMbF]`@GLGBIXv@J\\e@LI_@JCVGJ\\@fAC`@y@~\
EuAnH_ApG}@nEgEbP{AxGSfASXe@vD}@~H{@tICrABzAP~FLtANt@ZjAv@lBn@fAt@z@`F|Fh@j@rJvHpHfFvClC`AhAdAlApAfB\
j@hAp@~B\\`BJpAwKUeIMmAGoAQ}Ba@{A]c@MqEkBcFeBgBdGUf@sAzBm@~AI^I|@Af@R`CdAdM@PS@aCJsGZiQfAqBHqCn@w@X]P{BzBcEf\
EmCpCoBhBuHrHeG|FiAv@kBpAYJcHbCITkAr@mC`C}@~@uFbGQTu@nAc@l@u@r@MB{DdFoExGQ`@q@fA]X]HKAs@KgHyCeMaG}Aq@kAc@aBc@a\
ASFQjCp@z@VhD|AxDfB')")

a.execute("insert into lp_subscriber values (4,1433100432.606175,4040,\
'c|jnAeodyM{IhEsA_DcBqDWRj@tAzBxFz@lBzDjIlRz`@zInRt@jBbAdDz@fClEfL~JdX^dA\
bAdDfAjDJj@D\\Cv@Mn@[h@i@d@{BhAsDdBcBz@mAh@YHY@}BHoL^kKVqHTmNvAmDXuBHyBBsG\
ReM^iEJoELqBLgANa@Ne@ZYp@{AdFw@vB}@rCqA`EkF|Oi@hBc@jBy@`DeJnXsEjNsK|[yBtGeArCS\
p@UxAyA~My@jFe@jCw@hDo@|BcAbD{@dCa@l@[Ve@TcF`BqGbC{OlGsBt@wAn@sErB{BbAgAl@k@f@e@\
d@aAzA_AbBU`@[Rw@\\iCbBwFzDuAbA]\\SV[n@M\\Mx@Cj@?ZPxALnMLbIN|D?h@Gn@UpBObAe@\
xBk@`Ci@~Am@jASp@oCtQe@`C}@rFw@vGMbCCbAFhTAzGE|E@lCPrAPj@\\p@r@hAhD|EZn@Tp@Ln@BZ\
AtBCzGErAuArG}A~I@l@Pd@VTf@PNN`@HdARnDn@`Ed@lDZbAH?StAHlCJdAwI?c@NcCDu@FSf@iA^_BfAoFb@cCBo@FoAJ}@Lu@HuA`@gA')")

a.execute("insert into lp_subscriber values (5,1433100669.907712,5050,\
'uxjmAkhbyMrDoCfCeBnAs@nC}@fCs@fBg@GW}Bn@}@NmA^c@Tg@XwB~@kIxFZj@aUdPyLtImKpH]p\
@IFmDlC}NfKgDfCy@d@w@TcCtAoCvB}CtBsGhFuFdEyC~BqBxAkC|A}GhF_GfEsNhKyZdUeLlIyJlHaS|PkBpAa\
EzB__@vRiNhHgTdLwP|IgT|Kwe@|Vo[nPyEdC{FxC{CfBoB|@UX}@d@oGhDgTzK}GxCoCnAsCvAsC~AkAr@}DzCcI\
nD}DlBuBjAiDdBU^yCfB]i@i@wBm@uBY_AQa@UUSOa@UwGwBkAa@wRmF}OcFyFqBg@Yi@e@yBwCqJcMk@{@Ym@eAqCk@\
wBMy@C]AeABsBAoDE{BDcGAmCCc@[kB]gA[s@c@u@y@eAq@m@cBgAu`@ySmAq@wDmBmAg@y@Eq@@[DUFi@Z[R_@d@gC|Cc\
@^i@Xa@Li@Fy@Aq@QsEoB_EyBmHeDSEyAI_FMuBCgCB}B@kBAiGKuAKgBUs@AgJLkCHkC|G{A`Du@pAy@fAuArA{ArAm\
BvAg@`@g@PEKMGECgE[u@EGCEGOiFGcDK}CIwBMwB@oCCgCK}CGiG]eQ^iEkBK_Cc@aAI{@CEy@IeC')")

a.execute("insert into lp_subscriber values (6,1433100754.863967,6060,\
'atdnAgnsxMv@BbFr@pB^xC\\fBVhB\\v@Hr@E}@kBiBiD{B_DuBcDkBmDoBsFgAwCwAyDc@{@m@\
yAc@y@c@_AmAmDaAeDg@eBm@yAiBSlAkIn@gFpCgSz@cGHgAFkA?e@?UNAlEGdGYbFQvCObAWv\
Aa@hAa@\\QRc@F]w@mCIg@A_@@e@DiARyAz@}CbB_FVg@JMlEcBzGqCb@WPWxD_I\\mAv@wEbAoEpA}GB\
g@Ge@q@sCSeAO}A@m@NuBTgCf@iDRwBb@{Fr@oMBmAFcD`@sFLeAh@}IN_CPqBPaDp@cMf@cLV_PFsBJoINkF\
NuDJuC?}@_@gEKoB@gAPqB`@eEz@yGhAaJV{DP{CDgFZuLLaDPqBLy@V{@jAsDt@uB|GoQjDiJZcANaABy@A}@OyG\
mBw_@A[I_Ao@aH{@aIiFyh@WyBMyAGw@Au@@YBs@LgCJsChAu]TcJTeGLwBt@yUZcHTgN?sBCmFIcKEqNMqUCgCUi\
ISyH]yF}AuPo@}GGwA@qCBoDBcG?qCF{G?}FHmAh@eDDe@Ci@KYWc@e@k@g@c@sBu@iC_A{JmEyCkAu@CoAO}HeBoJsBqEcAoGmAgEk@uEa@@I')")

