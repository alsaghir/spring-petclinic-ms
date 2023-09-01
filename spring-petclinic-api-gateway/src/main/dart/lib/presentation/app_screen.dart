import 'package:flutter/material.dart';

/// A common widget acts as a container for the whole app.
/// Routes navigate to this widget and any pre or post
/// rendering logic goes here. Example of that is loading config
/// from yaml file and make sure it is loaded on startup
class AppScreen extends StatefulWidget {
  final Widget tabContent;

  const AppScreen({Key? key, required this.tabContent}) : super(key: key);

  @override
  State<AppScreen> createState() => _AppScreenState();
}

class _AppScreenState extends State<AppScreen> {

  @override
  Widget build(BuildContext context) {
    return widget.tabContent;
  }
}
