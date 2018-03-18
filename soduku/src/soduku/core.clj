(ns soduku.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :as ct])
  (:gen-class))

;; here are some soduku "solutions"
;; some of them are right, some of them are wrong
;;
;; to be compact, we represent these as 81 character strings (a 9x9 puzzle)
;;
(def oops-puzzles
  ["864371259325849761971265843436192587198657432257483916689734125713528694542916378"
   "346179258187523964529648371965832417472916835813754629798261543631485792254397186"
   "695127384138459672724836915851264739273981546946573821317692458489715263562348197"
   "497258316186439725253716498629381547375964182841572639962145873718623954534897261"
   "465912378189473562327568149738645291954821637216397854573284916642159783891736425"
   "194685237382974516657213489823491675541768923769352841215839764436527198978146352"
   "289765431317924856645138729763891542521473968894652173432519687956387214178246395"
   "894231657762495183351876942583624719219387564647159328128763495976542831435918276"
   "563472198219386754847195623472638519951247386638519472795864231324951867186723945"
   "163725948584693271729184365946358127371462589852917634498231756637549812215876493"
   "974183652651274389283596714129835476746912538835647921568329147317468295492751863"
   "431567289729481653865329174986243517257198346143756892612975438374812965598634721"
   "254367891893215674716984253532698147178432569649571328421753986365849712987126435"
   "958274163123698754746153928674315289532789416819462375285941637397526841461837592"
   "865379412924581376713642895397164528482795631156823947541236789679418253238957164"
   "865714329917362845234598761142657983783941256596283174358276492429835617671429538"
   "268495317194673852735128964872549631651387249943216785326951478589764123417832596"
   "947812563583764192261935478156349287879621354324587619698253741712496835435178926"
   "652483917978162435314975628825736149791824563436519872269348751547291386183657294"
   "712984365346751829589263471624179538853642917197538642978316254461825793235497186"
   "967254318184379562253186947691748235835621794742593681376415829428967153519832476"
   "251479638948316752637258194365124879712983465894567213423691587179845326586732941"
   "256734198891265374347198652514683729728519436963427581135942867689371245472856913"
   "964532178187694235235817964629451783573986412841273596416728359352169847798345621"
   "142569873385217946967438251536871429219654387478923165893746512621385794754192638"
   "598326147314957628672481935753648291421539876869172453285764319936215784147893562"
   "157468293238971645469532781926153478741896532583724916674385129892617354315249867"
   "927384165684915237531672489769231548453768921218459673175826394392147856846593712"
   "398256417476918253512743698185674329924835761763129845257361984831492576649587132"
   "176328459538149672492765138657834921924651387381972564813296745749583216265417893"
   "954738621276415389381692574837564912692183457415927863123846795569371248748259136"
   "183247596295638714647915832452396178718524369369871425921483657534769281876152943"
   "863915274429678351715324896287436915541892637936751482154283769698147523372569148"
   "276495183843271596951863247397528461168749325524316978635982714712654839489137652"
   "831245796726319548954768231419872365368451972275693814587134629193526487642987153"
   "168257439792436158543981672639874521415629783287315964924763815371548296856192347"
   "751432689293168574468759312587143961319675248624981735836524197145397826972816453"
   "318692547254781639697543128563974812721368954849125376176459283485236791932817465"
   "578213649263549178419867352724196835356482791891375264932651487685724913147938526"
   "956417832287536149341298765794153628815762394632984571178649253529371486463825917"
   "632951847814672953759348126391725684476189532285436791548217369927863415163594278"
   "285746913731859624946231587354687192812394756697125348179468235463512879528973461"
   "123897546956413872847265193691378254584921367732546918219634785368759421475182639"
   "851429376973658214264173589438517692629384751517962438346791825185236947792845163"
   "761495823284367159953128467625784931138259674497631582346512798579843216812976345"
   "638917452754326198219584376492873615571269843863145729185732964326491587947658231"
   "538742916694318527217956384871294653345671298962583471123869745786425139459137862"
   "687935241913247568452168793396721854278459316145386927569874132831692475724513689"
   "654837291389214756172965483896451327237689145541723869918346572723598614465172938"
   "192567438873429156564183279387942615251876394946351827739614582618295743425738961"
   "963752481451368972728491356892176534174539268635824197519647823387215649246983715"
   "851347692496582173723169458382675914514298367679413285148726539965834721237951846"
   "158479632963281475472563918215836794684792153397154826521948367839627541746315289"
   "187235496264891735395674182426389571719542863853716249938157624542968317671423958"
   "127398546654172398398564172769831254482659731531427689916745823273986415845213967"
   "925784613843621957716593824372159486598467132461238579634875291187942365259316748"
   "549638721726914853318572946654187392892453617137269584273845169965721438481396275"
   "612453798973618425854927163291784536435269817786135942527391684168542379349876251"
   "538972416761843952249561378412385769376294185985716234654127893193458627827639541"
   "287513964964782315351649782895236471716854293432197856543968127629471538178325649"
   "291845736638927154475136298327498561869751423154362879983574612712689345546213987"
   "537168942286943157914527836873659214145832769692714385328495671751286493469371528"
   "827314965619785432435692187142836759563947821978251346381529674754168293296473518"
   "643587219712936458958421763385269174476813925129745836831674592297358641564192387"
   "562834719314967285798215436973658142126349857485172963639781524251496378847523691"
   "431825796256397418978416235689134572725968341314572689143659827592783164867241953"
   "324857196617239548958614327143528769579463281862791435795346812236185974481972653"
   "743869152628751349951342867182674593579123486436985271865237914317498625294516738"
   "416853279529617843378924615145239786967548321832761594691382457254176938783495162"
   "784235619526971384319864257452398761137426895698517423941683572873152946265749138"
   "823957461415268397679314258958746123237591684146823579582439716764185932391672845"
   "831529467925674183476831529759416238183295746264783951698142375512367894347958612"
   "815947362724563198369218547287354619946721835531689724458196273193872456672435981"
   "654372981781946352932518467163294578428735196579681234296157843347829615815463729"
   "265748913943651728718239456451826379392475681876913245687594132129387564534162897"
   "918743652246951783375682194624178539591234867783596421859427316462315978137869245"
   "142978635376415928985632147421789563639254871857361294594127386268543719713896452"
   "143927685256184973897536142428359716731462859965871234379215468682743591514698327"
   "216597483984123756537648129673954218128736594459812367792365841841279635365481972"
   "278634951935271486416859237743915628681723549529486173394568712152397864867142395"
   "416957238732186954958342617695831742273469581841725369187593426324678195569214873"
   "298541367476382951351769248739426815162835479584197623613258794947613582825974136"
   "187623594453197872962845713376912458895374126214586937639758241548231679721469385"
   "194253687356874921827169435941528763538746192672391548483617259765982314219435876"
   "381457692942861375675932184163579428754286931829143567437618259596724813218395746"
   "958723641423651897176498532739245168684179325215386479847532916391867254562914783"
   "941238657673495821825167934319782546564319278782546319258974163436851792197623485"
   "983746125451298763762351894678423519145679238239185476314867952526914387897532641"
   "294785631513469728867231945382516479479823156156947382938672514745198263621354897"
   "184236957275419386963875124532648719891723645647591832728164593356982471419357268"
   "197684325628531974543792186372459861814367592956218437285173649469825713731946258"
   "932754681761982534485631729259176843873495216146823975594218367318567492627349158"
   "617823945245619873389547621851472369792368154463951287176235498528194736934786512"
   "627981543538247691419356782763498215845123967192675438974832156281569374356714829"
   "298361745534879612617245938361782594975614823842953176126537489459128367783496251"
   "687321549542869371913754862731246985854937126269518734395672418176485293428193657"
   "618732954925148763347956128839267415571493682462815379784321596153689247296574831"
   "392184576658379142714526839845962713179853264236417958487231695921645387563798421"
   "926437518785216943134859627342968175859721436617543892261395784573184269498672351"
   "647238519915476238328519467791652843563784921482391756839125674276843195154967382"
   "456971238821643795397285146634857912782169354519432687268394571173528469945716823"
   "267954813918236745543817629326579184189423576475168392891345267652791438734682951"])

;; session 1 - we made a function to convert a character to an integer
;; we leveraged that to make an array of integer out of the string

(defn- ch->int
  "converts a single char to integer value"
  [ch]
  (- (int ch) 48))

(defn- str->int
  [str]
  "converts a string of digits to array of int"
  (mapv ch->int str))

;; session 2 -
;; in soduko, the game consists of 9-element artifacts that are
;; rows, columns, or 3x3 cells.  The things we want to do are
;; related to these being handled similarly.
;;
;; thus, the next logical step is to develop some functions that let us
;; extract these artifacts from the game.
;;

;; we'll start learning how to do unit-testing, to help us along

(ct/deftest simple-pass
  (ct/is (> 2 1)))

(ct/deftest simple-fail
  true)

;; run your "test test" as:
(comment
  (ct/run-tests))

;; so, we want to define some primatives to fetch the "nth" row, column cell for n in [0..3)
;; which we will get to shortly

;; but now, for some TDD - test driven dentistry!

;; about range
(ct/deftest about-range
  (ct/is (= (range 3) '(0 1 2))))

;; about for
(ct/deftest about-for-1
  (ct/is (= (for [i (range 3)]
              '(0 1 2)))))

(ct/deftest about-for-2
  (ct/is (= (for [j (range 3)] (* 3 j))
            '(0 3 6))))

(ct/deftest about-for-3
  (ct/is (= (for [i (range 3) j (range 3)] [i j])
            '([0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2]))))

;; some more confusion - all of the above return abstract sequences, which appear as lists ('())
;; if you want to use vectors ([]) then you need to drag the sequences back into vectors
;; which is most easily accomplished with 'into'
(ct/deftest about-into
  (let [a-seq (range 3)
        a-vec (into [] a-seq)]
    (ct/is a-seq '(0 1 2))
    (ct/is a-vec [0 1 2])))

;;  so - we are ready to define 3 functions row-indicies, col-indicies,
;;  cell-indicies - these give us the indicies of the constituants

(defn row-indicies [i]
  (range (* i 9 ) (* 9 (inc i))))

(defn col-indicies [j]
  (let [base-range (range 0 81 9)
        bump (fn [x] (+ j x))]
    (mapv bump base-range)))

(defn cell-indicies [k]
  (let [r0 (* 3 (quot k 3))
        c0 (* 3 (mod k 3))]
    (for [i (range 3) j (range 3)]
      (+ (* 9 (+ r0 i))
         (+ c0 j)))))

(def test-game
  (str "598326147"
       "314957628"
       "672481935"
       
       "753648291"
       "421539876"
       "869172453"
       
       "285764319"
       "936215784"
       "147893562"))

(defn get-cell [i]
  (ch->int ((partial get test-game) i)))

(defn get-from [game]
  (fn [i] (ch->int ((partial get game) i))))

;; these we need to make work
(ct/deftest row-idx-test
  (ct/is (row-indicies 0)
         [0 1 2 3 4 5 6 7 9])
  (ct/is (row-indicies 2)
         [18 19 20 21 22 23 24 25 26]))

(ct/deftest col-idx-test
  (let [c0 (into [] (for [i (range 9)] (* 9 i)))
        c2 (map (fn [i] (* i 3)) c0)]
    (ct/is (col-indicies 0) c0)
    (ct/is (col-indicies 2) c2)))

(ct/deftest cell-idx-test
  (let [c0 [0 1 2  9 10 11  18 19 20]
        c4 (map #(+ 12 %) c0)
        c8 (map #(+ 12 %) c4)]
    (ct/is (cell-indicies 0)
           (cell-indicies 4))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
