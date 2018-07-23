Contribution Guidelines
=================================

First off all, thank you for taking the time to contribute! Pull requests are always welcome.
By contributing to this project
you are putting your work into the public domain (see [unlicense](LICENSE))


Code Style
----------
Please follow the code style of the Project.
This Project uss the IntelliJ default settings except:
```
code-style.kotlin.tabs-and-indents.continuation-indent = 4
```


Testing & Documentation
-------
Write unit and Integration Tests for your code

Run the tests! Write new ones and adapt them as needed.
Add or change the documentation where necessary.
* Add or change the API documentation
* Add or change the preject's readme


Version Control
---------------
When commiting code please make sure that you:
* Make commits of logical units.
* Be sure to use the issue key in the commit message.
* Ensure you have added and run the necessary tests for your changes.
* Run all the tests to assure nothing else was accidentally broken.

Prior to committing, please want to pull in the latest upstream changes.
Please use rebasing rather than merging.
Merging creates "merge commits" that pollute the project history.

* Push your changes to the topic branch in your fork of the repository.
* Initiate a pull request

### Commit Messages
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

Leave a blank line before the more detailed explanation. This block shall
be wrapped around a length of 72 characters. In git commands and other
contexts the first line will be treated as a header and everything else
will be considered as the content. The content is optional if the
subject line alone already offers enough explanation.

Resolves: #1
See also: #456, #789
````````````
