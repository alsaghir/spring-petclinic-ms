import 'package:flutter/material.dart';

class HomeTab extends StatelessWidget {
  const HomeTab({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
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
    );
  }
}