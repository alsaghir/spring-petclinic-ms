import 'package:flutter/material.dart';

import '../presentation/commons.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: Commons.appBar(),
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
