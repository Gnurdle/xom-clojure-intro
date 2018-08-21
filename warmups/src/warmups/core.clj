(ns warmups.core
  (:gen-class))


;; Stage 1: Data
;;
;; An extensible data notation - EDN primer
;; https://github.com/edn-format/edn
;;
;; building-blocks of "data":

;; the `nothing`
nil

;; bool
true false

;; numbers 
100   ;; int
100.5 ;; float
22/7  ;; ratio
123123123123123123123123123123123123123123123123123N ;; bigint (N)
3.1415926535897932384626433832795028841971693993751058209749445923078164M ;; bigdec (M)

;; strings
"hello" "now go \"away\""

;; chars 
\a \z \1 \*

;; regexs
#"^a.*z%"

;; the "extensible part" - tagged literals <tag> <str>, they can be passively conveyed.
#uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
#inst "2018-03-28T10:48:00.000"

;; Symbols
;; - start with non-numeric
;; - rest [:alphanum: *+!-_'?]
;; - (ignore leading quote for now, not part of symbol)
'x
'x42
'your-mom's-phone-number
'us+them
'*i-cant-hear-YOU*
'S...O---S...!
'my#munny->mybank!!!

;; the domain of symbols be kind of jolting to somebody that reads a lot of c-style code

;; Keywords (interns to itself, think 'enum')
:im-a-keyword :street-address :email

;; Namespaces
'math/pi :data/phone-number

;; Aggregates

'(1 "two" 3)                             ;; list, vis std::list (again that quote thing)
[10 "twenty" 30]                         ;; vector, vis std::vector
#{:a 14 101.2 "feline"}                  ;; set, vis std::set
{:a 10 :b "hello" 14 "world" :pi 22/7}   ;; map, vis std::map

;; all of these can be nested/composed arbitrarily
[{:a 10 :b "x"} ["one" "two" ["eleven" "twelve"]]]
{:name "Billy Bob"
 :us-citizen true
 :spouse "Crystal" :favorite-charity nil
 :smoker false :drinker true
 :id #uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
 :residence-type :trailer
 :children [{:name "scooter" :age 4 :gender :m}
            {:name "daisy"   :age 6 :gender :f}
            {:name "bubba"   :age 9 :gender :u}]
 :favorite-foods #{"beer" "bbq" "venison"}}

;; and that's all there is to EDN data

;; could think about it like a super-JSON, which adds boolean literals, ratio, bigint, bigdec
;; symbols, keywords, tagged literals, namespaces, and specificity in aggregations with
;; lists, vectors, sets, and maps.
;;
;; or not, JSON doesn't really do all that much...
;;
;; the proposal is that with these building blocks, you can model any kind of data-at-rest
;; found in the wild without having to water it down, or subject it to ambiguity induction
;; (like modelling dates as strings, not differentiating numeric types, only having two
;; (aggregation mechanisms)
;;
;; also a good balance between machine and human readability

;; -------------------------------------

;; Stage 2 - Homoiconicity -> using data to create code

;; with a couple of rules, we can use our ability to create data to create code,
;; which when evaluated, yield programs.
;;
;; the ability to write code in the same language as data has a number of benefits
;;   1) it's concise
;;   2) has almost no syntax at all
;;   3) it's ridiculously simple
;;
;; so the code rules:
;;
;;   1) literal data (mostly) evaluates to itself
;;   2) a symbol evaluates to the value bound to it
;;   3) a list evaluates to a "call" (sometimes called a 'form')
;;
;; since rules 2 and 3 make symbols and lists 'special', to specify them as data-literals in
;; code that gets evaluated, you have to quote them""

'freds-bed
'(1 grocery list)  ;; quoting applies to entire form

;; some forms that can be evaluated as code:
(+ 10 20)
(+ 5 (* 10 2))
(str "abc" "xyz")
(count [1 2 3 4 5])

;; '+' and '*' are just regular symbols here, nothing special is going on
;; the targets are +, *, str, count, the rest is just data

;; these "calls" to "targets" work like this:

;; (<target> [arg ...]) -> <return-value>

;; call the target, with some argument values, and get a value back (always)

;; since 'values' are recursively defined, all of this can be arbitrarily nested.

;; There are 3 types of "target".  Codewise, they look (and act) the same, but they work
;; differently:

;; 1)  args -->            <special-form>            --> result
;; 2)  args --> (eval) --> <function>                --> result
;; 3)  args -->            <macro>        --> (eval) --> result

;; so in the usual 'function' case, you evaluate all of the arguments first, and then
;; base those results into the function.
;;
;; the special forms implement cases where you may not want to blindly evaluate all the
;; arguments.  Think about short-circuiting, conditionals, declaratives, etc.
;;
;; macros take literal data, and can transform it (using the full language) into
;; code that is then subsequently evaluated.  Meta-programming using the full language,
;; and this is what makes homoiconicity so damn cool.
;;

;; some more:

;; 'if' is not a statement, it is special-form that returns the "then" or "else"
;; part depending on the test.  it acts like a function.
(if (= 10 (* 5 2)) "yup" "nope")

;; but, if we tear it down, we discover it has special behavior
(if (= 10 (* 5 2))
  (do
    (println "then-case")
    "yup")
  (do
    (println "else-case")))

;; nothing in the unused branch gets evaluated
;; since it acts like a function, it composes as one:
(str "answer is: " (if (= 10 (* 5 2)) "yup" "nope"))


;; -------------------------------------

;; Stage 3 - Bootstraping

;; special form 'def' - bind a symbol to an expression

(def aa 42)
(def bb "kittycat")
(def cc (str "merry-go-round" "broke " "down "))

(str aa bb cc)

;; functions - yeah, you knew this was coming....

(fn [a b]
  (+ a b))

;; what we have done was invoke 'fn', with first argument as a vector of symbols, and then a body
;; which have those symbols in it's scope.

;; nice, but where did that go?  It went *poof* right into the garbage pile

;; bind it to something to get it to stick around
(def add-em (fn [a b] (+ a b)))
(add-em 3 8)

;; common pattern, so 'defn' (define fn) is macro-based sugar
(defn add-em
  [a b]
  (+ a b))

;; an insightful peek under the sheets
(macroexpand '(defn add-em
                [a b]
                (+ a b)))

;; -------------------------------------
;; Stage 4 - getting functional

;; functions are first-class, aka they act like any other value,
;; so this construct of a map containing functions makes sense
;; because they are just values
(def my-ops
  {:add (fn [a b] (+ a b))
   :sub (fn [a b] (- a b))
   :mul #(* %1 %2)           ;; even terser way to get a function....
   :div #(/ %1 %2)})

;; --> 'get' is a function to lookup a value in an associative container

my-ops
(get my-ops :mul)
((get my-ops :mul) 7 4)

;; anything that implements the interface 'IFn' can be used as a function

;; --> 'ifn?' is a function that tests if the argument implements IFn or not

(ifn? +)
(ifn? get)
(ifn? (get my-ops :div))

;;but, hey now...
(def my-map {:goats 1 :bats 2 :cougars 3})
(def my-vec [1 2 3])
(def my-set #{:cat :dog :rat})

(ifn? :goats)   ;; does a keyword implement IFn ?
(:goats my-map) 

(ifn? my-map)   ;; does a map implement IFn ?
(my-map :cougars)

(ifn? my-set)   ;; how about a set ? 
(my-set :dog) (my-set :horse)

(ifn? my-vec)   ;; and a vector ?
(my-vec 0)

;; so
((:mul my-ops) 7 4)

;; higher order functions:
;; basically means functions can be passed as arguments, and
;; functions can return functions

(defn add-42 []
  (fn [a] (+ 42 a)))

(add-42)
((add-42) 10)

;; closures happen when functions reference outer scopes, and close over them
(defn add-something [something]
  (fn [a]
    (+ something a)))

(add-something 10)
((add-something 10) 5)

;; functions returning functions of other functions
((partial + 42) 5)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

@@@@
;; Stage 5 - transition from imperative to functional (this will be painful)

;; some insights:
;;
;;  - imperative programming consists of "a lot of do" - Stu Halloway
;; 
;;  - it has to do with banging and mutating specific places (memory and records)
;;
;;  - the words "memory" and "record" had meaning before I.T. co-opted them into their mythos
;;
;;  - Rich Hickey dubbed this "PLace Oriented Programming" (or PLOP)
;;
;;  - you can recognize that you are doing PLOP when things are changing over time
;;
;;  - objects are simply abstractions over places.
;;
;;  - we use things like 'git' ourselves, because if we used PLOP to manage our
;;    artifacts (source code),  we could never be able to reason about what the hell is going on.
;;
;;  - a core limitation of unbridled mutation is that any concurrent processing has to be
;;    coordinated with locks, to prevent things from getting into inconsistent states.  Locking
;;    is *HARD* and it doesn't compose - Stop the world while I look at it, or change it.
;;    as (n-1) cores sit on their hands...
;;
;;  - Inheritence was a bad dream that (most) people have woken up from.
;;
;;  - Stateful objects are the new "spaghetti code"
;;
;;







;; set this, read that, loop over this, etc.



;; loops, where are the loops?

;; the flow model is: <data> -> <process> -> <data>

;; loops are a process, and there is often much boilerplate:
;;
;; this imperative process is called 'map'
;;
;; function map(a-fn, seq){
;;    out = ()
;;    for(val : seq)
;;        out.conj( a-fn(val) );
;;    return out;
;; }

;; this imperative process is called 'filter'

;; function filter(pred, seq){
;;   out = ()
;;   for(val : seq){
;;     if( pred(val) )
;;       out.conj(val);
;;   return out;
;; }

;; this imperative process is called 'reduce'

;; function reduce(init, reducer, seq){
;;   out = init;
;;   for(val : seq){
;;     out = reducer(out, val);
;;   }
;;   return out;

;; map, filter, and reduce are higher order functions that do the same things
;; as the imparitive boilerplate forms above

(map (add-42) [1 2 3 4 5])
(filter odd? [1 2 3 4 5 6 7])
(reduce *  [1 2 3 4 5])

;; while these aren't every type of process, it happens that these cover a lot of it

;; things which don't fit these are handled by recursion

(defn fact [x]
  (if (< x 2)
    1
    (* x  (fact (dec x)))))

(fact 5M)

;; but we know the eventual peril of that lifestyle...
(fact 3000M)
(fact 30000M)

;; generally lisps handle this sort of thing with tail-call recursion -
;; if we know that if we are recursing from the tail of a function, there is no
;; need to keep the stack around, since it will never be used

;; clojure lives on JVM and javascript, neither of which support TCR

;; so there is a `hack` to simulate TCR
(defn fact2 [x]
  (loop [a 1 x x]
    (if (< x 2)
      a
      (recur (* x a) (dec x)))))

;; you cannot place recur anywhere that isn't in the tail position of a loop/function
;; so in some ways it's more helpful that presuming TCR is in play when it isn't

(def biggen (fact2 60000M))
(count (str biggen))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Stage 6 - laziness

;; mostly whenever possible, sequence-based processes result in lazy evaluation, which
;; means you don't actually calculate the values unless you need them

(range) ;; this is infinite
(nth (range) 350000)  ;; just pulled an element way in there

;; map and filter produce lazy sequences
;; reduce cannot, because it has to consume the entire sequence to produce the result

(take 25 (map #(* 100 %) (range)))
;;                ^^^ this gets called 25 times only, despite being fed an infinite sequence

;; a major clojure-specific lazyness feature is called 'transduction'
;; transduce means "carry-across" and it does that lazily

(def pa (map #(+ 7 %)))
(def pb (filter odd?))
(def pc (map #(+ 11 %)))

(take 7 (sequence (comp pa pb pc) (range)))

;; this setup a pipeline:
;; (range) -> (pa %) -> (pb %) -> (pc %) -> (take 7)
;; 
;; where the intermediate collections never existed, nor did the infinite range

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Stage 7 - Immutability and Purity

;; if 13 is a value, what happens to the 13 when you increment it?
(inc 13)

(def thirteen 13)
(inc thirteen)
thirteen

;; the 13 is still there - it is immutable data at rest

;; but what about this?

;; [13 14 15 ... 100000]

;; why would I be able to mutate that, but not that 13?
;; they are both immutable constants

;; in clojure, immutability is the default.  For EVERYTHING, including aggregates

;; incrementing a number gives a new number that is incremented, leaving the original alone
;; adding an item to a vector yields a new vector
;; assoc'ing a [kv] into a map makes a new map, and so on.
;;
;; once you have a value, including deeply nested/large composites, when you get it as a value,
;; nobody can change it, ever, not even you.

;; There are lots of advantages to working this way.
;;
;; Race condititions appear in software because of more than one thread of execution wants
;; to modify a 'place' at the same time.  If nobody can modify a place, then there are no race
;; conditions.
;;
;; This is addressed in imperative languages with locks, but locks don't compose very well.
;;
;; It means that once you have a piece of data, it is completely inert.  You can write it to a file,
;; you can email it to yourself and look at it later at home, you can give it to somebody and they
;; have everything they they need.
;;
;; programming with places means you have to restore said place to some state, somehow, so you
;; can go to that place and try to observe what was going on.
;; Often this is hard, or impossible to facilitate.

;;












