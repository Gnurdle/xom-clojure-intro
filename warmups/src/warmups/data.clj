(ns warmups.data)

;; Session 1: Data
;;
;; First we are going to talk about data.  Yeah, I know, you thought
;; we were going to start talking about programming languages.
;;
;; But you have biases (you think you know about programming languages,
;; and that they have syntax, and that they are all pretty much alike),
;; so indulge me here.  We have to unlearn first.
;;
;; stepping back, this is a REPL (read, eval, print, loop) exercise.
;; as I go through this, I'm going to read, evaluate, and print
;; the expressions as I go, using emacs - which is tightly coupled with
;; the REPL (you'll see why later).
;;
;; so DATA....
;;
;; the world of data is made up of the following building blocks:
;;

;; the `nothing` or `null` ... 
nil

;; bools
true false

;; numeric tower (numbers):
100   ;; int
100.5 ;; float
22/7  ;; ratio 
123123123123123123123123N ;; arbitrary precision integer (N)
3.1415926535897932384626433832795028841971 ;; and real (M)

;; characters
\a \z \1 \*

;; strings
"fred"
"hello"
"now go \"away\""

;; regular expressions
#"^a.*z%"

;; next, there are data-types we don't intrinsically know,
;; and we want to add new intrinsic types, like UUIDs and timestamps
;; these we preface with '#<type>'
#uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
#inst "2018-03-28T10:48:00.000"

;; Symbols (think of these as "stand-ins for other things"
;; rules:
;; - start with non-numeric
;; - rest [:alphanum: *+!-_'?]
;; - (leading quote is there becase we want the symbol, not what it
;;    references [the natural behavior of the REPL]).
'x
'x42
'your-mom's-phone-number
'us+them
'*i-can't-hear-YOU*
'S...O---S...!
'my#munny->mybank!!!

;; note - the domain of symbols be kind of jolting to somebody
;; that reads a lot of c-style code.  It's just data, there are no
;; notions of lexical tokens.  Data has none of that...

;; Keywords (interns to itself, think 'enum')
:im-a-keyword :street-address :email

;; Namespaces - notation <namespace>/<name>
'math/pi :data/phone-number 'math.com/PI

;; that's the end of the basic `atomic` types.
;;
;; We can probably agree, these are all VALUES

;; So, Aggregates - stuff built out of multiple `atomic`s

;; be aware that any value can be used to compose these

;; a list - in the classic LISP sense.  notiation (...)
;; compare to std::list - fast on the front, sloppy elsewhere
;; easy to append to front.
'(1 "two" 3)

;; a vector - compare to std::vector.  notation [...]
;; Randomly accessable, append easy to the end.
[10 "twenty" 30]

;; a set - compare to std::set, notation #{...}
;; unordered, holds only 1 of any value
#{:a 14 101.2 "feline"}

;; a map - compare to std::map, notation {...}
{:a 10 :b "hello" 14 "world" :pi 22/7}

;; so now you know EDN - an extensible data notation
;; this allows you to specify any DATA known to man.

;; all of these can be nested/composed arbitrarily
;; what follows is a single "value"
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

;; and that's all there is to EDN data.  It's a specification of how to compose
;; data literals consisting of:
;;    nil, bools, numbers, chars, strings, symbols, regexes extensible types,
;;    keywords, lists, vectors, sets, and maps.
;;
;; you could think about it like a super-JSON ...
;;
;; or not, JSON doesn't really do all that much...
;;
;; scalars are just as they seem,
;; () means a list,
;; [] means a vector,
;; {} means a map,
;; #{} means a set,
;; #"" means a regex,
;; #<tag> means a tagged literal.
;;
;; the proposal is that with these building blocks, you can model any kind
;; of data-at-rest found in the wild without having to water it down,
;; or subject it to ambiguity induction (like modelling dates as strings,
;; not differentiating numeric types, only having two, etc.)

;; aggregation mechanisms (object and vector)
;;
;; new atomic scalar types can be added.
;;
;; so that is EDN data - a good balance between machine and human readability


