(ns warmups.functional)

;; -------------------------------------
;; Session  4 - getting all functional

;; functions are first-class, aka they act like any other value
;;
;; for example (+ 3 5) and (fn [a b] (+ a b)) both return values
;;
;; the first is of type `integer`, the second is of type `function`
;;
;; so this construct of a map containing functions makes sense
;; because they are just values
(def my-ops
  {:add (fn [a b] (+ a b))
   :sub (fn [a b] (- a b))
   :mul #(* %1 %2)           ;; even terser way to get a function....
   :div #(/ %1 %2)})

;; --> 'get' is a function to lookup a value in an associative container (like a map)

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

;; yeah, all of that is pretty weird.  Our data structures are also
;; functions, which let us look things up in them when they are used
;; that way.  It's odd, but powerful...

;; -----------------------------------------------------------------------

;; higher order functions:
;; basically means functions can be passed as arguments, and
;; functions can return functions

;; functions return their last form as a value, right?
(defn fourty-two []
  42)
(fourty-two)

;; but, functions are values to, so
(defn add-42 []
  (fn [a] (+ 42 a)))
(add-42)
((add-42) 10)

;; `closures` happen when functions reference outer scopes, and close over them
(defn add-something [something]
  (fn [a]
    (+ something a)))

(add-something 10)
((add-something 10) 5)

;; functions returning functions of other functions
((partial + 42) 5)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Stage 5 - transition from imperative to functional mindset
;; (this will be painful)

;; some insights:
;;
;;  - imperative programming consists of "a lot of do" - Stu Halloway
;; 
;;  - it has to do with banging and mutating specific places (memory and
;;    records)
;;
;;  - the words "memory" and "record" had meaning before I.T. co-opted them
;;    into their mythos:
;;
;;  - Rich Hickey dubbed this "PLace Oriented Programming" (or PLOP)
;;
;;  - you can recognize that you are doing PLOP when things are changing
;;    over time
;;
;;  - objects are simply abstractions over places.
;;
;;  - we use things like 'git' ourselves, because if we used PLOP to manage
;;    our artifacts (source code),  we could never be able to reason about
;;    what the hell is going on.
;;
;;  - a core limitation of unbridled mutation is that any concurrent
;;    processing has to be coordinated with locks, to prevent things from
;;    getting into inconsistent states.  Locking is *HARD* and it doesn't
;;    compose - Stop the world while I look at it, or change it.
;;    as (n-1) cores sit on their hands...
;;
;;  - Inheritence was a bad dream that (most) people have woken up from.
;;
;;  - Stateful objects are the new "spaghetti code"
;;

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
;; as the imparitive boilerplate forms above, except they take functions
;; to fill in the blanks....

(map (add-42) [1 2 3 4 5])
(filter odd? [1 2 3 4 5 6 7])
(reduce *  [1 2 3 4 5])

;; while these aren't every type of process, it happens that these cover a
;; whole lot of them

;; things which don't fit these templates can be handled by recursion
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

;; you cannot place recur anywhere that isn't in the tail position of a
;; loop/function, so in some ways it's more helpful than presuming TCR is
;; in play when it isn't

(def biggen (fact2 60000M))
(count (str biggen))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Stage 6 - more abstractions
;;
;; we met the IFn abstraction previously (can be used as a function)
;;
;; there are more abstractions in clojure - here are some:
;;   ILookup    - can lookup by key (aka get)
;;   Counted    - has concept of count of items (aka count)
;;   ISeq       - can be coerced to a sequence (aka seq)
;;   IMapEntry  - key,value pair
;;   Indexed    - supports nth element (aka nth)
;;
;; most of these have to do with homoginizing the collection types
;;
;; for example `map` works on sequences, so it can take a list, set, map,
;; or vector

(map inc [1 2 3 4 5])
(map inc '(1 2 3 4 5))
(map inc #{1 2 3 4 5})

;; the sequence implemented by maps is [k v] pairs (IMapEntry)
(map (fn [map-entry] (inc (val map-entry))) {:a 1 :b 2 :c 3 :d 4 :e 5})

;; so a brief aside on data structures:

;; list - `first` is cheap, `last` is O(n), `nth` is linear time,
;; `conj` adds to front
;; vector - `first` is cheap, `last` is cheap, `nth` is cheap,
;; `conj` adds to end

;; a side-issue is that map 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Stage 7 - purity / data structures are persistent (aka immutable)
;;
;; in our working with functions, we have always produced an new value from an old value
;;
;; we have not modified any values, even the aggregates
;;
;; for example, if we do this:
;;
(def v1 [1 2 3 4 5])
(def v2 (conj v1 6))
v1
v2
;;
;; we have two vectors in play - the original, and the new one with '6' conjoined to it
;; the original is not modified.
;;
;; semantically, we are creating copies, and modifying the copy.
;;
;; it might look like this c++ code:
;;
;; std::vector<int> conj(const std::vector<int> in, int val){
;;     std::vector<int> out(in); // whoo-hoo copy-party!
;;     out.push_back(in, val);
;;     return out;
;; }
;;
;; but we all recognize the problem here.. while it might be sematically pleasing,
;; we are, in fact, making a copy of the original, modifying the copy, and returning that,
;; which is inefficient both computationally, and in terms of memory use.
;;
;; the sematics of this are beautiful - we don't need to care about who's affected by modifying
;; `in`.  The perfance, however, in general, sucks.  We would like to be able to do this, but
;; performance makes it prohibative in the general case.
;;
;; a cornerstone of clojure is, that even before the language happened, some serious research was
;; done to overcome this "lisp curse" and get the semantic benefits of values, without the performance
;; penalties.  Personally, I consider this "curse" to be the primary reason that imperative languages
;; won-out in the 60's and 70's over functional ones -- because this was an unsolved problem at the time.
;;
;; Clojure implements maps, vectors, sets, lists, as persistent data structures, which means that
;; when the copy+modify occurs, the result shares much of the structure of the original.
;; This does a decent job of slaying the "copy dragon".
;;
;; while this isn't as efficient as modifying in place, in Clojure the rule-of-thumb metric
;; is that writing is 10x slower, and reading is 4x slower than native.  While this doesn't solve
;; some of our problems, for most of them it falls well into the realm of "who cares"
;;
;; So what does persistent/immutable structures get us?
;;
;; 1) we no longer care about locking things - stuff that can't change doesn't need to be guarded by locks
;; 2) parallelization comes for free.  No locks, no blocks (free those cores!).
;; 3) it is *SO* much easier to reason about a system where things are immutable.
;; 4) pure functions, with no side effects, are much easier to debug, test, and reproduce.
;; 5) Since data-structures are pure, inert values, conveyance is trivial.
;;
;; a rather odd, but accurate piece of advice here, in clojure, lists are pretty much useless
;; since they share structure so poorly, and they are seldom used in the wild.
;; The workhorses are sets, maps, and vectors, all of which share structure nicely being implemented
;; as 32-way-tries.
;;
;; and one last note: the "tree falling in the forest" argument -
;;   clojure is a pragmatic language, so if you need to use a bang-in-place algorithm
;;   to beat the stuffing out of a transient data structure in a tight loop, there
;;   are provisions to enable this in a local context, to get performance and safety at the
;;   same time.
;;
;; and one more piece of pragmatism - clojure embraces side-effects, otherwise programs would be
;; pointless (unless you want to implement monads).  Clojure just helps by moving side-effects to
;; the edges, leaving most of the code pure, and easy to reason about.

;;;;;;;;;;;;;;;;;;;;;;
;; Stage 7 - laziness

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












