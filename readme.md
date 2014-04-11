### Building with Ant ###

Once you have the Android SDK installed along with the library dependencies,
run the following command from the root directory of the WordPress for Android
project:

    android update project -p .

This will create a `local.properties` file that is specific for your setup.
You can then build the project by running:

    ant debug

You can install the package onto a connected device or a virtual device by
running:

    ant installd

Also see the full Android documentation, [Building and Running from the Command
Line][command-line].

[command-line]: http://developer.android.com/tools/building/building-cmdline.html

## Run Unittests ##

    cd tests
    ant debug && ant installd && ant test
