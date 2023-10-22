import 'package:flutter/material.dart';
import 'package:url_launcher/link.dart';
import 'package:go_router/go_router.dart';

import 'constants.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final routes = Constants.kTabs;
    return Scaffold(
      appBar: AppBar(
        title: const Center(
          child: Text('Petclinic'),
        ),
        actions: List<Widget>.generate(
          routes.length,
              (index) => Link(
            uri: Uri.parse(routes[index].routeName),
            builder: (context, followLink) => ElevatedButton(
              autofocus: GoRouterState.of(context).uri.toString().substring(1) ==
                  routes[index].routeName
                  ? true
                  : false,
              onPressed: () =>
                  GoRouter.of(context).goNamed(routes[index].routeName),
              child: Text(routes[index].tabName),
            ),
          ),
        ),
      ),
      body: SafeArea(
        child: Center(
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 100),
              child: Column(
                children: [
                  Text(
                    'Welcome to Pet Clinic',
                    style: Theme.of(context).textTheme.headlineMedium,
                  ),
                  const Image(
                      fit: BoxFit.scaleDown,
                      image: AssetImage('assets/images/pets.png'))
                ],
              ),
            ),

        ),
      ),
    );
  }
}
