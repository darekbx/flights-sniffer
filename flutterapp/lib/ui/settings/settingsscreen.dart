import 'package:flutter/material.dart';
import 'package:flutterapp/ui/boundsselect/boundsselectscreen.dart';

class SettingsScreen extends StatelessWidget {

  SettingsScreen();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("Settings")),
        body: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("Settings"),
              ElevatedButton(child: Text("Open bounds select"), onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) =>
                    BoundsSelectScreen()));
              }),
            ]
        ));
  }
}
