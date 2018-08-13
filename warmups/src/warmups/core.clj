(ns warmups.core
  (:gen-class))

;; >> comments start with ';;' <<

;; Stage-1 - human-> text -> reader -> data -> life

;; aka "it's all about the data"

;; conversion of text to data is accomplished by the "reader"
;;
;; these are things the reader eats for breakfast.  There are very few special characters/delimiters

;; atomic literals
nil
true
false

;; int / float
100
100.5

;; ratio type
22/7

;; bigint type
123123123123123123123123123123123123123123123123123N

;; bigdecimal type
3.1415926535897932384626433832795028841971693993751058209749445923078164M

;; strings
"hello"

;; char
\a \z

;; regex
#"^a.*z%"

;; tagged elements (a thing that might mean something to certain consumers
;; by can also be handled losslessly by a neutral carrier).
#uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"

;; a keyword (think 'enum')
:im-a-keyword :street-address :email

;; a symbol - ignore the (') protecting it from being evaluated for now
'this-is-a-symbol 'us+them '*i-cant-hear-YOU*
'...---... 'my#munny->mybank!!!

;; composition with containers - hetrogenous collection types
;; containers are also atomic, so they compose arbitrarily.

;; lists are set off by ( ), they have (std::list) type behaviors
;; (again, ignore the quote, we'll get to that later)
'(1 2 3)
'(X Y Z)
'(:this "is" Fun)

;; vectors are set off by [ ], they have (std::vector) type behaviors
[1 2 3]
[:a :b :c 42.4]
["the" :dog :goes :out :at 11.43M]

;; sets are set off by #{ }, they are associative containers of keys (std::set)
#{:a 14 101.2 "feline"}

;; maps are set off by { }, they are associative containers of key/value pairs (std::map)
{:a 10 :b "hello" 14 "world" :pi 22/7}

;; all of these can be nested/composed arbitrarily
[ {:a 10 :b "x"} ["one" "two" ["eleven" "twelve"]]]

{:name "Billy Bob"
 :us-citizen :true
 :spouse "Krystal"
 :id #uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
 :residence-type :trailer
 :children [{:name "scooter" :age 4 :gender :m}
            {:name "daisy"   :age 6 :gender :f}
            {:name "bubba"   :age 9 :gender :u}]
 :favorite-foods #{"beer" "bbq" "venison"}}

;; this is all "just data"

;; you could see doing something like that in JSON or XML
;; only you'd lose some information by homogonizing everything to
;; objects or string types.  EDN keeps all the structure together

;; -------------------------------------

;; Stage 2 - Homoiconicity -> using data to write code

;; we have been "evaluating" forms all the way through this, they
;; just happen to be data constants.
;;
;; so the trick to using data to write code is to establish a convention
;; which tells us how to "evaluate" the data.
;;
;; - symbols evaluate to whatever they are bound to
;;
;; - lists are treated as S-Expressions:
;;
;;    (<op> [<args...])
;;
;; - anything else evaluates to itself (it just data)
;;
;; side-note: that this explains the quoting (') that showed up briefly above.
;; since we are evaluating forms, the evaluator treats lists and symbols
;; specially, because it's trying to evaluate them.  by quoting these,
;; you tell the evaluator "hands off".
;;
;; It happens that <op> can be one of several things:
;;
;; - most often, it's a symbol, bound to a function, which means that you evaluate
;;   it by first evaluating the arguments, passing them to the function, and yielding
;;   what said function function returns.
;;
;; - it can be a macro, which means we want to replace the form with
;;   whatever the macro expands to, and then evaluate that (recursively)
;;
;; - it can be a so-called "special form", which means it acts like
;;   a function, but has some semantic rules that need to be taken
;;   into account aside from blindly evaluating all the args first
;;   (short-circuiting for example).
;;
;; that's it for syntax - we are done.
;;
;; let's write some programs

+ ;; a core function called '+', which happens to add numbers nothing special, '+' is a symbol

(+ 10 20)

(+ 5 (* 10 2))
(str "abc" "xyz")

(conj [1 2 3] 4)

(assoc {:a 10 :b 20} :c 30)

(dissoc {:a 10 :b 20} :a)

;; everything takes args, returns a new value

;; 'if' is not a statement, it is a function that returns the "then" or "else"
;; part depending on the test
(if (= 10 (* 5 2)) "yup" "nope")

;; since it acts like a function, it composes as one:
(str "answer is: " (if (= 10 (* 5 2)) "yup" "nope"))

;; everything in clojure follows this pattern.

=======================================

;; special form 'def' - bind a symbol to an expression

(def aa 42)
(def bb "kittycat")
(def cc (str "merry-go-round" "broke " "down "))

(str aa bb cc)

;; functions - yeah, you knew that was coming....

(fn [a b]
  (+ a b))

;; nice, but where did that go?

(def addem (fn [a b] (+ a b)))
(addem 3 8)

;; so common, we have a 'defn' (define fn)
(defn addem
  [a b]
  (+ a b))

;; functions are first-class, aka they act like any other value-type, so this construct makes sense

(def my-ops
  {:add (fn [a b] (+ a b))
   :sub (fn [a b] (- a b))
   :mul #(* %1 %2)           ;; even terser way to get a function....
   :div #(/ %1 %2)})

my-ops
(get my-ops :mul)
((get my-ops :mul) 7 4)

;; anything that implements the interface 'IFn' can be used as a function

(ifn? +)
(ifn? get)
(ifn? (get my-ops :div))

;;but?
(def my-map {:goats 1 :bats 2 :cougars 3})
(def my-vec [1 2 3])
(def my-set #{:cat :dog :rat})

(ifn? :goats)
(:goats my-map)

(ifn? my-map)
(my-map :cougars)

(ifn? my-set)
(my-set :dog) (my-set :horse)

(ifn? my-vec)
(my-vec 0)

;; so
((:mul my-ops) 7 4)

;; higher order functions
;; basically means functions can be passed as arguments
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

;; loops, where are the loops?

;; the flow model is:    <data> -> <process> -> <data>

;; loops are a process, and there is often much boilerplate
;;
;; this imperative process is called 'map'
;;
;; function map(a-fn, seq){
;;    out = ()
;;    for(val : seq)
;;        out.append( a-fn(val) );
;;    return out;
;; }

;; this imperative sequence is called 'filter'

;; function filter(pred, seq){
;;   out = ()
;;   for(val : seq){
;;     if( pred(val) )
;;       out.append(val);
;;   return out;
;; }

;; this imperative sequence is called 'reduce'

;; function reduce(init, reducer, seq){
;;   out = init;
;;   for(val : seq){
;;     out = reducer(out, val);
;;   }
;;   return out;

;; map, filter, and reduce are higher order functions that do the same things

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

;; generally lisps handle this sort of thing with tail-call recursion, e.g.
;; we know that if we are recursing from the tail of a function, there is no
;; need to keep the stack around, since it will never be used

;; clojure lives on JVM and javascript, neither of which support TCR

;; so there is a hack to do get out of this corner
(defn fact2 [x]
  (loop [a 1 x x]
    (if (< x 2)
      a
      (recur (* x a) (dec x)))))

(def biggen (fact2 60000M))
(count (str biggen))








