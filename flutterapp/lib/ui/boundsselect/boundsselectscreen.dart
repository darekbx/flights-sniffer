import 'package:flutter/material.dart';

class BoundsSelectScreen extends StatelessWidget {

  BoundsSelectScreen();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("Bounds Select")),
        body: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("Bounds Select")
            ]
        ));
  }
}
