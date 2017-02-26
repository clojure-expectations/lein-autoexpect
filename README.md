# autoexpect

Leiningen plug-in for automatically running [expectations](https://github.com/jaycfields/expectations) whenever your Clojure project's source changes.

If you are using expectations clojure.test compatible syntax, you'll want to use [lein-test-refresh](https://github.com/jakemcc/lein-test-refresh).

## Features

- Allows you to have extremely fast feedback cycles by automatically
  loading changed code and running your expectations.
- Supports growl notifications of test status. `lein autoexpect :growl`
- Supports OS X notifications using `terminal-notifier`: `lein autoexpect :notify`.
- Supports only notifying when test status changes by adding command
  line flag to either of the above commands. `lein autoexpect :notify :change-only`

## Usage

Here is what using it looks like. 

    $ lein autoexpect
    *********************************************
    *************** Running tests ***************
    Ran 3 tests containing 3 assertions in 16 msecs
    0 failures, 0 errors.

Your terminal will just stay like that. Every half second autoexpect
polls the file system to see if anything has changed. When there is a
change your code is tested again.

If you want to receive notifications using growl, then run `lein
autoexpect :growl`. This has been tested with modern versions of Growl
for [OS X](http://growl.info/),
[Linux](http://mattn.github.com/growl-for-linux/), and
[Windows](http://growlforwindows.com/).

If you would like to use the OS X notification center, use `lein autoexpect :notify`.
This requires [terminal-notifier] (https://github.com/alloy/terminal-notifier), which you can install using `brew install terminal-notifier`.


### Latest version

The version in the image below is the latest (and hopefully greatest) released version of `lein-autoexpect`. It is what version number should be used in any of the verion numbers specified lower than this point in the README.

[![Latest version](https://clojars.org/lein-autoexpect/latest-version.svg)](https://clojars.org/lein-autoexpect)

### Using with Leiningen 2.0

Add `[lein-autoexpect "1.9.0"]` to your `~/.lein/profiles.clj` as
follows:

    {:user {:plugins [[lein-autoexpect "1.9.0"]]}}
    
Alternatively add to your `:plugins` vector in your project.clj file.
   
    (defproject sample
      :dependencies [[org.clojure/clojure "1.9.0"]]
      :profile {:dev {:dependencies [[expectations "2.0.9"]]}}
      :plugins [[lein-autoexpect "1.9.0"]])

## Compatibility

autoexpect should work with any version of expectations. If there is
an issue please report it. It has been tested it with versions 1.1.0,
1.3.[023678], and 1.4.*, and 2.0.9.

Because of
[tools.namespace](https://github.com/clojure/tools.namespace) changes
`lein-autoexpect` requires that your project use Clojure >= 1.3.0. If
your project also depends on a version of `tools.namespace` < 0.2.1
you may see occasional exceptions.

## License

Copyright (C) 2011-2016 [Jake McCrary](http://jakemccrary.com)

Distributed under the Eclipse Public License, the same as Clojure.


