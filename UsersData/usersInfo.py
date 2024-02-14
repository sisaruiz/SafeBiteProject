from faker import Faker
import json
import secrets
import random
from datetime import date

def convert_to_str(obj):
    if isinstance(obj, date):
        return obj.isoformat()
    return obj

def generate_random_users(num_users):
    fake = Faker()
    users = []

    for _ in range(num_users):
        user = {
            'user_name': fake.user_name(),
            'email': fake.email(),
            'password': secrets.token_hex(8),
            'country': fake.country(),
            'date_of_birth': fake.date_of_birth(minimum_age=18, maximum_age=80),
            'gender': random.choice(['Male', 'Female', 'Other'])
        }
        users.append(user)

    return users

if __name__ == "__main__":
    num_users_to_generate = 200
    random_users = generate_random_users(num_users_to_generate)

    # Convert date_of_birth to string representation
    for user in random_users:
        user['date_of_birth'] = convert_to_str(user['date_of_birth'])

    # Save the generated users to a JSON file
    with open('random_users2.json', 'w') as file:
        json.dump(random_users, file, default=convert_to_str, indent=2)

    print(f"{num_users_to_generate} random users generated and saved to 'random_users.json'.")
