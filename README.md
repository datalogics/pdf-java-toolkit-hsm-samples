# Datalogics PDF Java Toolkit Client Project

_[TODO: Replace above name of this project.]_

_[TODO: Add a description of this project here.]_


## Requirements

* Java SE 1.7
* Maven 3.3.9
* _[TODO: Add any additional requirements.]_

## Using with an evaluation version of PDFJT

_[TODO: Omit or modify this section if this project will not require a license managed version. Note that the following material should be true for any project supporting the license managed PDFJT.]_

The evaluation version of PDF Java Toolkit has license management, and a different artifact name: ``pdfjt-lm``. There's also a corresponding ``talkeetna-lm`` which similarly depends on ``pdfjt-lm``. Switching to use these versions of PDF Java Toolkit and Talkeetna is provided with Maven profiles.

### License file

Evaluation copies will come with a license file, with a name ending in ``.l4j``.

Move the license file to the top-level directory of the samples, so that it is in the current directory when running samples.

To use license-managed PDFJT, create a file called ``.use-pdfjt-lm`` in the top directory of this project. This will activate profiles automatically for Maven and Eclipse.

If this project was received as part of an evaluation, then the ``.use-pdfjt-lm`` file is already created.

## Tested IDEs

_[TODO: Update which IDEs are supported.]_

The samples were developed and tested using [Mars 4.5.0](https://eclipse.org/mars/)

## Using remote input PDF files

_[TODO: Omit this section if not relevant.]_

By default, the samples run with pre-packaged input PDF files that are treated as resources of the samples. However, you can use your own input PDF files including those that are stored remotely on a shared network with the samples. As long as the shared network where the PDF files reside is mounted on your local machine it's treated a part of your local file system.

For more information on working with files in java please see [file path java tutorial](http://www.java2s.com/Tutorial/Java/0180__File/UniversalNamingConvention.htm).

## Contributing

See ``CONTRIBUTING.md``
