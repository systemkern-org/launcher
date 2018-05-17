# Contribution Guidelines
First off all, thank you for taking the time to contribute! By contributing to this project
you are putting your work into the public domain (see [unlicense](LICENSE))

Push requests are of course always welcome.


## Version Control

When commiting code please make sure that you:
* Make commits of logical units.
* Be sure to use the issue key in the commit message.
* Ensure you have added and run the necessary tests for your changes.
* Run all the tests to assure nothing else was accidentally broken.

Prior to committing, please want to pull in the latest upstream changes
also please use rebasing rather than merging.
Merging creates "merge commits" that pollute the project history.

* Push your changes to the topic branch in your fork of the repository.
* Initiate a pull request

## Commit Messages

**Why do good commit messages matter?**

While browsing any random git repository you will often find its commit
messages not following any pattern and ending up more or less in a mess.
A good example for this are the early commit messages in the spring repo.

**Can you easily identify each commit?**

```
$ git log --oneline -5 --author cbeams --before "Fri Mar 26 2009"

e5f4b49 Re-adding ConfigurationPostProcessorTests after its brief removal
in r814. @Ignore-ing the testCglibClassesAreLoadedJustInTimeForEnhancement()
method as it turns out this was one of the culprits in the recent build
breakage. The classloader hacking causes subtle downstream effects,
breaking unrelated tests. The test method is still useful, but should
only be run on a manual basis to ensure CGLIB is not prematurely
classloaded, and should not be run as part of the automated build.
2db0f12 fixed two build-breaking issues: + reverted ClassMetadataReading
Visitor to revision 794 + eliminated ConfigurationPostProcessorTests
until further investigation determines why it causes downstream tests to
 fail (such as the seemingly unrelated ClassPathXmlApplicationContextTests)
147709f Tweaks to package-info.java files
22b25e0 Consolidated Util and MutableAnnotationUtils classes into
existing AsmUtils
7f96f57 polishing
```

As compared to some more recent commits:

```
$ git log --oneline -5 --author pwebb --before "Sat Aug 30 2014"

5ba3db6 Fix failing CompositePropertySourceTests
84564a0 Rework @PropertySource early parsing logic
e142fd1 Add tests for ImportSelector meta-data
887815f Update docbook dependency and generate epub
ac8326d Polish mockito usage
```

**Which one is easier to read?**

The first example lacks any consistent form, lenght or style,
the second is consisten and concise.
The first one is what happens naturally if there is no system,
the second is deliberately managed and enforced.


### What is the purpose of commit message
Since the diff of a commit easily shows you what has changed, the purpose
of the commit message is to tell why it has changed. The message should
establish a context of why the change was made. This makes it easier for
your fellow developer (most likely yourselves) to understand the changes.

A good commit message reduces wasteful work and ultimately shows if you
the author are a good collaborator.

### Benefits
Think about tools like `git blame, `git reverse`, `git log` and
`shortlog`.

Of course you can use these with any repository in any
style but since they are practicyally uselsess it is very unlikely that
anyone will actually do this.

But in a well formatted, well cared for repo these commands can deliver
their full potential and become worth using. Understanding why something
happened some months or years ago becomes not only possible but can be
done independently without the help of the original developer.

### Shower Thoughts

If a project becomes a long term success is highly dependent on how
maintainable its codebase is. A maintainer has few tools that are more
powerfull than VCS log. This makes it worth the time to care for it.
Ultimately it is a source of productivity.

## Style
Almost every programming language, IDE and even most projects well-establised
conventions on how to write and structure code, name variables, and
organize atrefacts. Even if there is disagreement on which style to choose
almost every developer agrees that it is far better to choose any standard
and sticking to it, than to endure the chaos that ensues when everyone
does their own thing.

**Content:** What kind of information must be present in a commit message,
what kind of indormation can be presen. Also equally important what kind
of information _must not_ be present.

**Syntax and Grammar:** To reduce guesswork about markups, line lenghts, captialization,
punctuation and grammar should be well defined in a simple manner. This
ensures uniformity and reduces (or even eliminates) guesswork.
The result will be a suprisingly consistent log which can be read.
A log that can be read is a log that will be read on a regular basis.

**Metadata:** How should external information like issue numbers, pull request ids,
etc. be included?


## Rules
Please keep in mind, this is nothing new, this is not invented by me, it
has been said before and it will be said again
> * [tbaggery](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)
> * [git-scm](https://www.git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project#_commit_guidelines)
> * [Thorvalds](https://github.com/torvalds/subsurface-for-dirk/blob/master/README#L92-L120)
> * [who-t](http://who-t.blogspot.co.at/2009/12/on-commit-messages.html)
> * [Github](https://github.com/erlang/otp/wiki/writing-good-commit-messages)
> * [Spring Framework](https://github.com/spring-projects/spring-framework/blob/30bce7/CONTRIBUTING.md#format-commit-messages)
> * [Chris Beams](https://chris.beams.io/posts/git-commit/)


1. Start the subject line with the ticket number followed by an uppercase
2. Limit the length of the subject line to 50 characters
3. Use the imperative in the subject line
4. No period at the end of the subject
5. Leave a blank line after the subject for separation
6. Limit the leght of the body to 72 characters
7. Explain the why and the context of the change, not what was changed


Example:

````````````
#1 Summarize changes with a max length of 50

Leafe a blank line before the more detailed explanation. This block shall
be wrapped around a length of 72 characters. In git commands and other
contexts the first line will be treated as a header and everything else
will be considered as the content. The content is optional if the
subject line alone already offers enough explanation.

Use imperative in the subject line. This makes the message more concise
and easier to parse. The subject line if written correctly finishes the
phrase "This commit will ..."
Capitalize the first word of the subject line and do *NOT* terminate it
with a dot. In the Content of course

Wrapping the body at 72 characters is necessary because git itselve does
not automatically wrap lines while outputting to the command line. The
recommendation is to do this at 72 so that there is still enough space
left over for git to put commit hashes into the log while still staying
below the very common 80 character overall limit.

While explainng why you have implemented the committed changes you can
 * Use bullet points for formatting
 * add issue numbers from your issue tracker at the bottom as well

Resolves: #1
See also: #456, #789
````````````
