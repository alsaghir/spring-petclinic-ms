import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/link.dart';

import '../presentation/constants.dart';

class Commons {
  static AppBar appBar() {
    final routes = Constants.kTabs;
    return AppBar(
      title: const Center(
        child: Text('Petclinic'),
      ),
      actions: List<Widget>.generate(
        routes.length,
        (index) => Link(
          uri: Uri.parse(routes[index].routeName),
          builder: (context, followLink) => ElevatedButton(
            autofocus: GoRouter.of(context).location.substring(1) ==
                    routes[index].routeName
                ? true
                : false,
            onPressed: () =>
                GoRouter.of(context).goNamed(routes[index].routeName),
            child: Text(routes[index].tabName),
          ),
        ),
      ),
    );
  }

}

@immutable
class Config {
  const Config({required this.kBackendHost, required this.kOwnersApi});

  final String kBackendHost;
  final String kOwnersApi;

  String ownersEndpoint() {
    return kBackendHost + kOwnersApi;
  }
}