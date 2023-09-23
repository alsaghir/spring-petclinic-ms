import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            centerTitle: false,
            automaticallyImplyLeading: false,
            title: const Text('Owner Information'),
            leading: IconButton(
              onPressed: () {},
              icon: const Icon(Icons.arrow_back),
            ),
          ),
          body: CustomScrollView(slivers: [
            SliverFillRemaining(
              hasScrollBody: false,
              child: Column(children: [
                Container(),
                Container(
                    width: double.infinity,
                    height: 100,
                    child: Row(children: [
                      Text("My Identification"),
                    ])),
                Flexible(
                    fit: FlexFit.loose,
                    child: Container(
                      child: SingleChildScrollView(
                        padding: EdgeInsets.only(top: 100),
                        scrollDirection: Axis.horizontal,
                        child: Column(
                          children: [
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                            Text("My Identification"),
                          ],
                        ),
                      ),
                    )),
              ]),
            ),
          ])),
    );
  }
}
