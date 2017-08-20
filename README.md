Naive Background Correction 1.0.0
=================================

A simple plugin for ImageJ 1.x to perform a very basic background brightness correction. It works by subtracting / dividing a highly blurred version of the image from itself. 

This was mainly an excuse for me to explore the ImageJ plugin API.

![Animated picture showing operation of the plugin](Screenshots/preview.gif?raw=true)

Installation
-----

Drop Naive_Background_Correction.class in your plugins subfolder of your ImageJ installation.

Usage
-----

Choose the 'Naive Background Correction' option in the plugins menu. Adjust the iterations and radius values to insure a complete bluring of the original image (this can be checked by enabling the preview). Choose if you want to subtract or divide the background (or produce both) and then click OK.

Known issues
------------

Sometimes when typing in more than one digit into the Iteration input box, the previw dosen't update. I'm not sure why this is. Clicking the arrows to increase and then decrease the iteration count will refresh the preview.

License
-------

Naive Background Correction is copyrighted free software made available under the terms of the Expat License

Copyright: (C) 2017 by Keith Offer. All Rights Reserved.
