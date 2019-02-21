(ns warmups.bootstrapping)

;; -------------------------------------

;; Session 3 - Bootstrapping

;; special form 'def' - bind a symbol to an expression.
'' Bindings are what `bind` values to symbols.

(def aa 42)
(def bb "kittycat")
(def cc (str "merry-go-round" " broke " "down."))

(str aa bb cc)

;; and functions - yeah, you knew this was coming....

(fn [a b]
  (+ a b))

;; what we have done was invoke the 'fn' special form, with first argument as
;; a vector of symbols, and then a body which have those symbols bound to the
'' arguments (in it's scope).

;; the return value of a function is result of evaluating the last form

;; nice, but where did that go?  It went *poof* right into the garbage pile,
;; just like any other value that floats away.

;; now we bind it to something to get it to stick around
(def add-em (fn [a b] (+ a b)))
(add-em 3 8)

;; common pattern, so 'defn' (define fn) is macro-based sugar to
;; make this common pattern straight foward.
(defn add-em
  [a b]
  (+ a b))

;; an insightful peek under the (macro) sheets
(macroexpand '(defn add-em
                [a b]
                (+ a b)))

;; so with that, we can define values (and bind them to a symbol),
;; and some of these values are `functions` which we also bind to
;; a symbol, which makes them usable in our code.
