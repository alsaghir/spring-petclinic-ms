import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:petclinic_ui/screens/app_screen.dart';
import 'package:petclinic_ui/screens/home_tab.dart';
import 'package:petclinic_ui/screens/owners_tab.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    // GoRouter configuration
    var router = GoRouter(
      initialLocation: '/owners',
      routes: [
        GoRoute(
          path: '/home',
          builder: (context, state) => AppScreen(tabContent: HomeTab()),
        ),
        GoRoute(
          path: '/owners',
          builder: (context, state) => ChangeNotifierProvider(
            create: (context) => OwnerListState(),
            child: AppScreen(
              tabContent: OwnersTab(),
            ),
          ),
        ),
        GoRoute(
          path: '/veterinarians',
          builder: (context, state) => AppScreen(tabContent: Text('Vater')),
        ),
        GoRoute(
          path: '/error',
          builder: (context, state) => AppScreen(tabContent: Text('Errors')),
        ),
      ],
    );

    return MaterialApp.router(
      debugShowCheckedModeBanner: false,
      routerConfig: router,
      title: "Pet Clinic App",
    );
  }
}
