import 'dart:io';
import 'dart:typed_data';

import 'package:fory/fory.dart';

part 'hello_fory.fory.dart';

/// Namespace-qualified type name shared by every language sample.
const String typeName = 'org.feuyeux.fory.UserInfo';

const List<String> langs = <String>[
  'java',
  'kotlin',
  'scala',
  'go',
  'js',
  'python',
  'rust',
  'csharp',
  'dart',
  'cpp',
];

@ForyStruct()
class UserInfo {
  UserInfo();

  int userId = 0;

  String name = '';

  // int32 on the wire, matching every other language sample
  @ForyField(type: Int32Type())
  int age = 0;

  List<String> emails = <String>[];

  Map<String, String> properties = <String, String>{};

  @ListField(element: Int32Type())
  List<int> scores = <int>[];

  bool active = false;

  int createdAt = 0;

  // Uint8List maps to Fory's BINARY blob
  Uint8List avatar = Uint8List(0);

  @override
  String toString() =>
      'UserInfo(userId=$userId, name=$name, age=$age, emails=$emails, '
      'properties=$properties, scores=$scores, active=$active, '
      'createdAt=$createdAt, avatar=$avatar)';
}

UserInfo createSampleUserInfo() => UserInfo()
  ..userId = 12345
  ..name = 'John Doe'
  ..age = 30
  ..emails = <String>['john.doe@example.com', 'johndoe@gmail.com']
  ..properties = <String, String>{
    'city': 'San Francisco',
    'country': 'USA',
    'role': 'Developer',
  }
  ..scores = <int>[85, 90, 78, 92]
  ..active = true
  ..createdAt = DateTime.now().millisecondsSinceEpoch ~/ 1000
  ..avatar = Uint8List.fromList(<int>[0, 1, 2, 3, 4, 5]);

void main() {
  // Create output directory if it doesn't exist
  final Directory outputDir = Directory('../workspace');
  outputDir.createSync(recursive: true);

  // Create a sample UserInfo object
  final UserInfo userInfo = createSampleUserInfo();
  print('Created user: $userInfo');

  // Create fory instance configured for cross-language compatibility
  // (Dart is xlang-only and compatible mode is on by default)
  final Fory fory = Fory(compatible: true);
  // Register under the shared namespace-qualified name instead of the Dart type name.
  HelloForyForyModule.register(fory, UserInfo, name: typeName);

  // Serialize the user to bytes
  final Uint8List serialized = fory.serialize(userInfo, trackRef: true);

  // Write the serialized data to a file
  final File outputFile = File('${outputDir.path}/userinfo_dart.fory');
  outputFile.writeAsBytesSync(serialized);
  print('Serialized to ${outputFile.path}');

  // Try to read serialized data from other languages
  for (final String lang in langs) {
    final File langFile = File('${outputDir.path}/userinfo_$lang.fory');
    if (!langFile.existsSync()) {
      continue;
    }
    try {
      final UserInfo fromFile = fory.deserialize<UserInfo>(langFile.readAsBytesSync());
      print('Deserialized [$lang] user info from ${langFile.path}:$fromFile');
    } catch (e) {
      print('Failed to deserialize [$lang]: $e');
    }
  }
}
