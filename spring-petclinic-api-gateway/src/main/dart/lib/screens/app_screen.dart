import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:petclinic_ui/config.dart';
import 'package:url_launcher/link.dart';

class AppScreen extends StatefulWidget {
  final Widget tabContent;

  const AppScreen({Key? key, required this.tabContent}) : super(key: key);

  @override
  State<AppScreen> createState() => _AppScreenState();
}

class _AppScreenState extends State<AppScreen> {
  final _routes = [
    Tab('Home', '/home'),
    Tab('Owners', '/owners'),
    Tab('Veterinarians', '/veterinarians'),
    Tab('Error', '/error')
  ];

  @override
  void initState() {
    super.initState();
    Future.delayed(Duration.zero, () {
      Config.loadAssets(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Center(
          child: Text('Petclinic'),
        ),
        actions: List<Widget>.generate(
          _routes.length,
              (index) =>
              Link(
                uri: Uri.parse(_routes[index].urlPath),
                builder: (context, followLink) =>
                    ElevatedButton(
                      autofocus: GoRouter
                          .of(context)
                          .location == _routes[index].urlPath
                          ? true
                          : false,
                      onPressed: followLink,
                      child: Text(_routes[index].tabName),
                    ),
              ),
        ),
      ),
      body: FutureBuilder<bool>(future: Config.isLoaded.future,
        builder: (context, snapshot) {
          if (snapshot.hasData)
            return SafeArea(child: widget.tabContent,);
          else
            return CircularProgressIndicator();
        },),
    );
  }
}

class Tab {
  final String tabName;
  final String urlPath;

  Tab(this.tabName, this.urlPath);
}
